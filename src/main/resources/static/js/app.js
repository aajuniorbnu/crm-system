let clientesCache = [];
const appBaseUrl = (() => {
    const base = document.body?.dataset?.baseUrl || "/";
    return base.endsWith("/") ? base : `${base}/`;
})();

document.addEventListener("DOMContentLoaded", async () => {
    configurarMenuMobile();
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

function filtrarClientes() {
    const term = document.getElementById("searchCliente").value.toLowerCase();
    const status = document.getElementById("filterClienteStatus").value;
    filtrarLinhas("#clientesTableBody tr", (row) => {
        const search = row.dataset.search?.toLowerCase() || "";
        const rowStatus = row.dataset.status || "";
        return search.includes(term) && (!status || rowStatus === status);
    });
}

function filtrarLinhas(selector, predicate) {
    document.querySelectorAll(selector).forEach((row) => {
        row.style.display = predicate(row) ? "" : "none";
    });
}

async function gerarRelatorios() {
    try {
        const clientes = await apiRequest(appUrl("api/clientes"));

        const inicio = document.getElementById("dataInicioRelatorio").value;
        const fim = document.getElementById("dataFimRelatorio").value;
        const clientesPeriodo = clientes.filter((cliente) => {
            if (!cliente.dataCadastro) {
                return false;
            }
            const date = cliente.dataCadastro.slice(0, 10);
            return (!inicio || date >= inicio) && (!fim || date <= fim);
        });

        atualizarResumoRelatorio(clientes, clientesPeriodo);
        atualizarTabelaClientes(clientesPeriodo);
        atualizarStatusClientes(clientes);
    } catch (error) {
        console.error(error);
        showMessage("Nao foi possivel gerar os relatorios.");
    }
}

function atualizarResumoRelatorio(clientes, clientesPeriodo) {
    const clientesAtivos = clientes.filter((cliente) => cliente.status === "ATIVO").length;
    const prospectos = clientes.filter((cliente) => cliente.status === "PROSPECTO").length;
    document.getElementById("totalClientesRelatorio").textContent = clientes.length;
    document.getElementById("clientesAtivosRelatorio").textContent = clientesAtivos;
    document.getElementById("novosClientesRelatorio").textContent = clientesPeriodo.length;
    document.getElementById("clientesProspectoRelatorio").textContent = prospectos;
}

function atualizarTabelaClientes(clientes) {
    const rows = clientes
        .slice()
        .sort((a, b) => (a.nome || "").localeCompare(b.nome || ""))
        .slice(0, 10)
        .map((cliente) => `
            <tr>
                <td>${cliente.nome || "-"}</td>
                <td>${cliente.email || "-"}</td>
                <td>${cliente.status || "-"}</td>
            </tr>
        `)
        .join("");

    document.getElementById("topClientesRelatorio").innerHTML =
        rows || '<tr><td colspan="3" class="empty-state">Nenhum cliente no periodo.</td></tr>';
}

function atualizarStatusClientes(clientes) {
    const totalPorStatus = {
        ATIVO: 0,
        PROSPECTO: 0,
        INATIVO: 0,
        BLOQUEADO: 0
    };

    clientes.forEach((cliente) => {
        if (totalPorStatus[cliente.status] !== undefined) {
            totalPorStatus[cliente.status] += 1;
        }
    });

    document.getElementById("clientesAtivos").textContent = totalPorStatus.ATIVO;
    document.getElementById("clientesProspectos").textContent = totalPorStatus.PROSPECTO;
    document.getElementById("clientesInativos").textContent = totalPorStatus.INATIVO;
    document.getElementById("clientesBloqueados").textContent = totalPorStatus.BLOQUEADO;
}

function exportarCSV(tipo) {
    const rows = [];
    rows.push(["Cliente", "Email", "Status"]);
    document.querySelectorAll("#topClientesRelatorio tr").forEach((row) => {
        const cols = Array.from(row.querySelectorAll("td")).map((col) => col.textContent.trim());
        if (cols.length === 3) {
            rows.push(cols);
        }
    });

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
