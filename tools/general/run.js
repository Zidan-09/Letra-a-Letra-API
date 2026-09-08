import { TestContext } from "../context/TestsContext.js";
import { runFlow as leftOnMatch } from "./leftOnMatch.js";

const registry = {
    leftOnMatch,
    "leftOnMatch": leftOnMatch,
    "left-on-match": leftOnMatch,
    "left_on_match": leftOnMatch,
};

function printHelp() {
    console.log(`
Usage:
  node tools/general/run.js <testName>   - executa um teste específico da pasta general
  node tools/general/run.js --list       - lista testes disponíveis
  node tools/general/run.js --help       - mostra esta ajuda

Testes disponíveis em tools/general:
${Object.keys(registry).map(k => `  - ${k}`).join("\n")}

Exemplos:
  node tools/general/run.js leftOnMatch
  node tools/general/leftOnMatch.js        (execução direta do arquivo)

Cada teste em general é isolado (não sequencial como tools/runner.js ou tools/seed.js).
O runner cria um TestContext com 2 usuários, autentica e conecta os sockets
antes de chamar runFlow(context), igual ao fluxo de matchmaking.
`);
}

const arg = process.argv[2];

if (!arg || arg === "--help" || arg === "-h" || arg === "help") {
    printHelp();
    process.exit(0);
}

if (arg === "--list" || arg === "-l" || arg === "list") {
    console.log("Testes disponíveis em tools/general:");
    const seen = new Set();
    for (const [name, fn] of Object.entries(registry)) {
        if (seen.has(fn)) continue;
        seen.add(fn);
        console.log(`  - ${name}`);
    }
    process.exit(0);
}

// resolve alias
const flow = registry[arg];

if (!flow) {
    console.error(`\n❌ Teste "${arg}" não encontrado em tools/general\n`);
    printHelp();
    process.exit(1);
}

console.log(`\n--------Init General Test: ${arg} --------\n`);

const context = new TestContext();

// leftOnMatch precisa de 2 usuários (igual matchmaking). Para futuros testes
// com necessidades diferentes, o próprio runFlow pode validar context.users
context.addUser("general1");
context.addUser("general2");

let exitCode = 0;

try {
    await context.authUsers();
    await context.connectSockets();

    await flow(context);

    console.log("\n✅  General test OK:", arg);
} catch (e) {
    console.error("\n❌  General test FAIL:", arg);
    console.error(e);
    exitCode = 1;
} finally {
    try {
        context.dispose();
    } catch {}
    // dá tempo para fechar sockets antes de encerrar o processo
    setTimeout(() => process.exit(exitCode), 500);
}
