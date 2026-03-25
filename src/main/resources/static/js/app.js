let clientesCache = [];
const appBaseUrl = (() => {
    const base = document.body?.dataset?.baseUrl || "/";
    return base.endsWith("/") ? base : `${base}/`;
})();

document.addEventListener("DOMContentLoaded", async () => {
    configurarMenuMobile();
    await carregarClientesParaSelects();
    configurarCalculoVenda();
    configurarDatasRelatorio();
    focarSecaoAtiva();
});

function appUrl(path) {
    const normalized = String(path || "").replace(/^\/+/, "");
    return `${appBaseUrl}${normalized}`;
}

function configurarMenuMobile() {
    const toggle = document.getElementById("menuToggle");
    if (!toggle) {
        return;
    }

    toggle.addEventListener("click", () => {
        document.body.classList.toggle("sidebar-open");
    });

    document.querySelectorAll(".nav-stack a").forEach((link) => {
        link.addEventListener("click", () => {
            document.body.classList.remove("sidebar-open");
        });
    });
}

async function apiRequest(url, options = {}) {
    const response = await fetch(url, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        },
        ...options
    });

    if (!response.ok) {
        throw new Error(`Erro ${response.status}`);
    }

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

function focarSecaoAtiva() {
    const pagina = document.body.dataset.pagina;
    const map = {
        dashboard: "dashboard-section",
        clientes: "clientes-section",
        produtos: "produtos-section",
        vendas: "vendas-section",
        tickets: "tickets-section",
        relatorios: "relatorios-section"
    };

    const targetId = map[pagina];
    const target = targetId ? document.getElementById(targetId) : null;

    if (target) {
        requestAnimationFrame(() => {
            target.scrollIntoView({ behavior: "smooth", block: "start" });
        });
    }
}

function configurarCalculoVenda() {
    const valorTotal = document.querySelector('#formVenda [name="valorTotal"]');
    const desconto = document.querySelector('#formVenda [name="desconto"]');
    [valorTotal, desconto].forEach((input) => {
        if (input) {
            input.addEventListener("input", calcularValorFinalVenda);
        }
    });
    calcularValorFinalVenda();
}

function calcularValorFinalVenda() {
    const valorTotal = parseFloat(document.querySelector('#formVenda [name="valorTotal"]').value) || 0;
    const desconto = parseFloat(document.querySelector('#formVenda [name="desconto"]').value) || 0;
    const valorFinal = Math.max(valorTotal - desconto, 0);
    document.querySelector('#formVenda [name="valorFinal"]').value = valorFinal.toFixed(2);
}

async function carregarClientesParaSelects() {
    try {
        clientesCache = await apiRequest(appUrl("api/clientes"));
        const vendaSelect = document.getElementById("vendaClienteSelect");
        const ticketSelect = document.getElementById("ticketClienteSelect");
        [vendaSelect, ticketSelect].forEach((select) => popularSelectClientes(select));
    } catch (error) {
        console.error(error);
    }
}

function popularSelectClientes(select) {
    if (!select) {
        return;
    }

    select.innerHTML = '<option value="">Selecione</option>';
    clientesCache.forEach((cliente) => {
        const option = document.createElement("option");
        option.value = cliente.id;
        option.textContent = cliente.nome;
        select.appendChild(option);
    });
}

function configurarDatasRelatorio() {
    const inicio = document.getElementById("dataInicioRelatorio");
    const fim = document.getElementById("dataFimRelatorio");
    if (!inicio || !fim) {
        return;
    }

    const hoje = new Date();
    const primeiroDia = new Date(hoje.getFullYear(), hoje.getMonth(), 1);
    inicio.value = primeiroDia.toISOString().split("T")[0];
    fim.value = hoje.toISOString().split("T")[0];
}

function resetForm(formId, defaults = {}) {
    const form = document.getElementById(formId);
    form.reset();
    form.querySelector('[name="id"]').value = "";
    Object.entries(defaults).forEach(([name, value]) => {
        const field = form.querySelector(`[name="${name}"]`);
        if (field) {
            field.value = value;
        }
    });
}

function resetClienteForm() {
    resetForm("formCliente", { tipo: "PESSOA_FISICA", status: "ATIVO" });
}

function resetProdutoForm() {
    resetForm("formProduto", { unidadeMedida: "UN", estoqueMinimo: 10, status: "ATIVO" });
}

function resetVendaForm() {
    resetForm("formVenda", { desconto: 0, status: "PENDENTE", formaPagamento: "PIX" });
    calcularValorFinalVenda();
}

function resetTicketForm() {
    resetForm("formTicket", { prioridade: "MEDIA", status: "ABERTO" });
}

function preencherFormulario(formId, data, mapping = {}) {
    const form = document.getElementById(formId);
    Object.entries(data).forEach(([key, value]) => {
        const fieldName = mapping[key] || key;
        const field = form.querySelector(`[name="${fieldName}"]`);
        if (field && value !== null && value !== undefined) {
            field.value = value;
        }
    });
}

function getJsonFromForm(formId) {
    const form = document.getElementById(formId);
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());
    if (!data.id) {
        delete data.id;
    }
    return data;
}

function showMessage(message) {
    window.alert(message);
}

function openModal(modalId) {
    const modal = bootstrap.Modal.getOrCreateInstance(document.getElementById(modalId));
    modal.show();
}

function closeModal(modalId) {
    const modal = bootstrap.Modal.getInstance(document.getElementById(modalId));
    if (modal) {
        modal.hide();
    }
}

async function salvarCliente() {
    try {
        const data = getJsonFromForm("formCliente");
        const method = data.id ? "PUT" : "POST";
        const url = data.id ? appUrl(`api/clientes/${data.id}`) : appUrl("api/clientes");
        await apiRequest(url, { method, body: JSON.stringify(data) });
        showMessage("Cliente salvo com sucesso.");
        location.reload();
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel salvar o cliente.");
    }
}

async function editarCliente(id) {
    try {
        resetClienteForm();
        const cliente = await apiRequest(appUrl(`api/clientes/${id}`));
        preencherFormulario("formCliente", cliente);
        openModal("modalCliente");
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel carregar o cliente.");
    }
}

async function verCliente(id) {
    try {
        const cliente = await apiRequest(appUrl(`api/clientes/${id}`));
        showMessage(`Cliente: ${cliente.nome}\nEmail: ${cliente.email}\nTelefone: ${cliente.telefone || "-"}`);
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel buscar o cliente.");
    }
}

async function deletarCliente(id) {
    if (!confirm("Deseja excluir este cliente?")) {
        return;
    }
    try {
        await apiRequest(appUrl(`api/clientes/${id}`), { method: "DELETE" });
        showMessage("Cliente excluido com sucesso.");
        location.reload();
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel excluir o cliente.");
    }
}

async function salvarProduto() {
    try {
        const data = getJsonFromForm("formProduto");
        ["preco"].forEach((field) => {
            if (data[field] !== undefined) {
                data[field] = Number(data[field]);
            }
        });
        ["estoque", "estoqueMinimo"].forEach((field) => {
            if (data[field] !== undefined) {
                data[field] = parseInt(data[field], 10);
            }
        });
        const method = data.id ? "PUT" : "POST";
        const url = data.id ? appUrl(`api/produtos/${data.id}`) : appUrl("api/produtos");
        await apiRequest(url, { method, body: JSON.stringify(data) });
        showMessage("Produto salvo com sucesso.");
        location.reload();
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel salvar o produto.");
    }
}

async function editarProduto(id) {
    try {
        resetProdutoForm();
        const produto = await apiRequest(appUrl(`api/produtos/${id}`));
        preencherFormulario("formProduto", produto);
        openModal("modalProduto");
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel carregar o produto.");
    }
}

async function verProduto(id) {
    try {
        const produto = await apiRequest(appUrl(`api/produtos/${id}`));
        showMessage(`Produto: ${produto.nome}\nCodigo: ${produto.codigo}\nPreco: ${formatCurrency(produto.preco)}\nEstoque: ${produto.estoque}`);
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel buscar o produto.");
    }
}

async function deletarProduto(id) {
    if (!confirm("Deseja excluir este produto?")) {
        return;
    }
    try {
        await apiRequest(appUrl(`api/produtos/${id}`), { method: "DELETE" });
        showMessage("Produto excluido com sucesso.");
        location.reload();
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel excluir o produto.");
    }
}

async function salvarVenda() {
    try {
        const data = getJsonFromForm("formVenda");
        data.cliente = { id: parseInt(data["cliente.id"], 10) };
        delete data["cliente.id"];
        ["valorTotal", "desconto", "valorFinal"].forEach((field) => {
            data[field] = Number(data[field] || 0);
        });
        const method = data.id ? "PUT" : "POST";
        const url = data.id ? appUrl(`api/vendas/${data.id}`) : appUrl("api/vendas");
        await apiRequest(url, { method, body: JSON.stringify(data) });
        showMessage("Venda salva com sucesso.");
        location.reload();
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel salvar a venda.");
    }
}

async function editarVenda(id) {
    try {
        resetVendaForm();
        const venda = await apiRequest(appUrl(`api/vendas/${id}`));
        preencherFormulario("formVenda", venda);
        document.querySelector('#formVenda [name="cliente.id"]').value = venda.cliente ? venda.cliente.id : "";
        calcularValorFinalVenda();
        openModal("modalVenda");
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel carregar a venda.");
    }
}

async function verVenda(id) {
    try {
        const venda = await apiRequest(appUrl(`api/vendas/${id}`));
        showMessage(`Venda #${venda.id}\nCliente: ${venda.cliente ? venda.cliente.nome : "-"}\nValor final: ${formatCurrency(venda.valorFinal)}\nStatus: ${venda.status}`);
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel buscar a venda.");
    }
}

async function deletarVenda(id) {
    if (!confirm("Deseja excluir esta venda?")) {
        return;
    }
    try {
        await apiRequest(appUrl(`api/vendas/${id}`), { method: "DELETE" });
        showMessage("Venda excluida com sucesso.");
        location.reload();
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel excluir a venda.");
    }
}

async function salvarTicket() {
    try {
        const data = getJsonFromForm("formTicket");
        data.cliente = { id: parseInt(data["cliente.id"], 10) };
        delete data["cliente.id"];
        const method = data.id ? "PUT" : "POST";
        const url = data.id ? appUrl(`api/tickets/${data.id}`) : appUrl("api/tickets");
        await apiRequest(url, { method, body: JSON.stringify(data) });
        showMessage("Ticket salvo com sucesso.");
        location.reload();
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel salvar o ticket.");
    }
}

async function editarTicket(id) {
    try {
        resetTicketForm();
        const ticket = await apiRequest(appUrl(`api/tickets/${id}`));
        preencherFormulario("formTicket", ticket);
        document.querySelector('#formTicket [name="cliente.id"]').value = ticket.cliente ? ticket.cliente.id : "";
        openModal("modalTicket");
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel carregar o ticket.");
    }
}

async function verTicket(id) {
    try {
        const ticket = await apiRequest(appUrl(`api/tickets/${id}`));
        showMessage(`Ticket #${ticket.id}\nCliente: ${ticket.cliente ? ticket.cliente.nome : "-"}\nAssunto: ${ticket.assunto}\nStatus: ${ticket.status}`);
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel buscar o ticket.");
    }
}

async function deletarTicket(id) {
    if (!confirm("Deseja excluir este ticket?")) {
        return;
    }
    try {
        await apiRequest(appUrl(`api/tickets/${id}`), { method: "DELETE" });
        showMessage("Ticket excluido com sucesso.");
        location.reload();
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel excluir o ticket.");
    }
}

function filtrarClientes() {
    const term = document.getElementById("searchCliente").value.toLowerCase();
    const status = document.getElementById("filterClienteStatus").value;
    filtrarLinhas("#clientesTableBody tr", (row) => {
        const search = row.dataset.search?.toLowerCase() || "";
        const rowStatus = row.dataset.status || "";
        return search.includes(term) && (!status || rowStatus === status);
    });
}

function filtrarProdutos() {
    const term = document.getElementById("searchProduto").value.toLowerCase();
    const status = document.getElementById("filterProdutoStatus").value;
    filtrarLinhas("#produtosTableBody tr", (row) => {
        const search = row.dataset.search?.toLowerCase() || "";
        const rowStatus = row.dataset.status || "";
        return search.includes(term) && (!status || rowStatus === status);
    });
}

function filtrarVendas() {
    const status = document.getElementById("filterVendaStatus").value;
    const inicio = document.getElementById("dataInicioVenda").value;
    const fim = document.getElementById("dataFimVenda").value;
    filtrarLinhas("#vendasTableBody tr", (row) => {
        const rowStatus = row.dataset.status || "";
        const rowDate = row.dataset.date || "";
        const matchStatus = !status || rowStatus === status;
        const matchInicio = !inicio || rowDate >= inicio;
        const matchFim = !fim || rowDate <= fim;
        return matchStatus && matchInicio && matchFim;
    });
}

function filtrarTickets() {
    const term = document.getElementById("searchTicket").value.toLowerCase();
    const prioridade = document.getElementById("filterTicketPrioridade").value;
    const status = document.getElementById("filterTicketStatus").value;
    filtrarLinhas("#ticketsTableBody tr", (row) => {
        const search = row.dataset.search?.toLowerCase() || "";
        const rowStatus = row.dataset.status || "";
        const rowPrioridade = row.dataset.priority || "";
        return search.includes(term) &&
            (!status || rowStatus === status) &&
            (!prioridade || rowPrioridade === prioridade);
    });
}

function filtrarLinhas(selector, predicate) {
    document.querySelectorAll(selector).forEach((row) => {
        row.style.display = predicate(row) ? "" : "none";
    });
}

async function gerarRelatorios() {
    try {
        const [vendas, tickets, clientes] = await Promise.all([
            apiRequest(appUrl("api/vendas")),
            apiRequest(appUrl("api/tickets")),
            apiRequest(appUrl("api/clientes"))
        ]);

        const inicio = document.getElementById("dataInicioRelatorio").value;
        const fim = document.getElementById("dataFimRelatorio").value;
        const vendasPeriodo = vendas.filter((venda) => {
            if (!venda.dataVenda) {
                return false;
            }
            const date = venda.dataVenda.slice(0, 10);
            return (!inicio || date >= inicio) && (!fim || date <= fim);
        });

        const ticketsPeriodo = tickets.filter((ticket) => {
            if (!ticket.dataAbertura) {
                return false;
            }
            const date = ticket.dataAbertura.slice(0, 10);
            return (!inicio || date >= inicio) && (!fim || date <= fim);
        });

        const clientesPeriodo = clientes.filter((cliente) => {
            if (!cliente.dataCadastro) {
                return false;
            }
            const date = cliente.dataCadastro.slice(0, 10);
            return (!inicio || date >= inicio) && (!fim || date <= fim);
        });

        atualizarResumoRelatorio(vendasPeriodo, ticketsPeriodo, clientesPeriodo);
        atualizarTopClientes(vendasPeriodo);
        atualizarStatusVendas(vendasPeriodo);
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel gerar os relatorios.");
    }
}

function atualizarResumoRelatorio(vendas, tickets, clientes) {
    const faturamento = vendas.reduce((acc, venda) => acc + (venda.valorFinal || 0), 0);
    const ticketsResolvidos = tickets.filter((ticket) => ticket.status === "RESOLVIDO" || ticket.status === "FECHADO").length;
    document.getElementById("totalVendasRelatorio").textContent = vendas.length;
    document.getElementById("faturamentoRelatorio").textContent = formatCurrency(faturamento);
    document.getElementById("novosClientesRelatorio").textContent = clientes.length;
    document.getElementById("ticketsResolvidosRelatorio").textContent = ticketsResolvidos;
}

function atualizarTopClientes(vendas) {
    const agrupado = new Map();
    vendas.forEach((venda) => {
        const nome = venda.cliente ? venda.cliente.nome : "Sem cliente";
        if (!agrupado.has(nome)) {
            agrupado.set(nome, { nome, totalCompras: 0, valor: 0 });
        }
        const item = agrupado.get(nome);
        item.totalCompras += 1;
        item.valor += venda.valorFinal || 0;
    });

    const rows = Array.from(agrupado.values())
        .sort((a, b) => b.valor - a.valor)
        .slice(0, 10)
        .map((item) => `
            <tr>
                <td>${item.nome}</td>
                <td>${item.totalCompras}</td>
                <td>${formatCurrency(item.valor)}</td>
            </tr>
        `)
        .join("");

    document.getElementById("topClientesRelatorio").innerHTML =
        rows || '<tr><td colspan="3" class="empty-state">Nenhuma venda no periodo.</td></tr>';
}

function atualizarStatusVendas(vendas) {
    const totalPorStatus = {
        PENDENTE: 0,
        APROVADA: 0,
        CANCELADA: 0,
        ENTREGUE: 0
    };

    vendas.forEach((venda) => {
        if (totalPorStatus[venda.status] !== undefined) {
            totalPorStatus[venda.status] += 1;
        }
    });

    document.getElementById("vendasPendentes").textContent = totalPorStatus.PENDENTE;
    document.getElementById("vendasAprovadas").textContent = totalPorStatus.APROVADA;
    document.getElementById("vendasCanceladas").textContent = totalPorStatus.CANCELADA;
    document.getElementById("vendasEntregues").textContent = totalPorStatus.ENTREGUE;
}

function exportarCSV(tipo) {
    const rows = [];
    if (tipo === "clientes") {
        rows.push(["Cliente", "Compras", "Valor"]);
        document.querySelectorAll("#topClientesRelatorio tr").forEach((row) => {
            const cols = Array.from(row.querySelectorAll("td")).map((col) => col.textContent.trim());
            if (cols.length === 3) {
                rows.push(cols);
            }
        });
    } else {
        rows.push(["Status", "Quantidade"]);
        rows.push(["PENDENTE", document.getElementById("vendasPendentes").textContent]);
        rows.push(["APROVADA", document.getElementById("vendasAprovadas").textContent]);
        rows.push(["CANCELADA", document.getElementById("vendasCanceladas").textContent]);
        rows.push(["ENTREGUE", document.getElementById("vendasEntregues").textContent]);
    }

    const csv = rows.map((row) => row.join(";")).join("\n");
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8;" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = `relatorio-${tipo}.csv`;
    link.click();
}

function exportarPDF() {
    window.print();
}

function formatCurrency(value) {
    return new Intl.NumberFormat("pt-BR", {
        style: "currency",
        currency: "BRL"
    }).format(Number(value || 0));
}
