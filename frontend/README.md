# VistaPrime Frontend

Frontend Angular da experiencia imobiliaria do projeto.

## O que existe aqui

- catalogo responsivo para `venda` e `aluguel`
- filtros por categoria e cidade
- calculadora de valor por metro quadrado
- painel administrativo para cadastrar, editar e excluir imoveis
- integracao com a API Spring em `/api/imoveis`

## Rodando em desenvolvimento

Use preferencialmente Node `20` ou `22`.

```bash
npm install --ignore-scripts
npm start
```

O `npm start` usa `proxy.conf.json` para redirecionar chamadas `/api` para `http://localhost:8080`.

## Validacao

Checagem de compilacao Angular sem bundling:

```bash
npx ngc -p tsconfig.app.json
```

Observacao: neste workspace, o `Node 25.3.0` causa falha nativa no `ng build` com `esbuild`, entao para build final use um Node LTS suportado pelo Angular.
