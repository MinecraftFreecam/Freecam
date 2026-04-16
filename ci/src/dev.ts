import { spawnSync } from "node:child_process";

const GHA = process.env.GITHUB_ACTIONS === "true";

function error(msg: string) {
  if (GHA) console.error(`::error::${msg}`);
  else console.error(msg);
}

function run(cmd: string[]): number {
  console.error("+", cmd.join(" "));
  const arg0 = cmd[0];
  if (!arg0) return 0;

  const result = spawnSync(arg0, cmd.slice(1), { stdio: "inherit" });
  const code = result.status ?? 1;

  if (code !== 0) error(`FAILED: exit code ${code}`);
  return code;
}

function runAll(cmds: string[][]) {
  const results = cmds.map(run);
  const failures = results.filter((c) => c !== 0).length;

  if (failures > 0) {
    error(`${failures} failure${failures > 1 ? "s" : ""} occurred`);
    process.exit(1);
  }
}

if (import.meta.main) {
  runAll([
    ["npm", "run", "lint"],
    ["npm", "run", "typecheck"],
    ["node", "--test"],
  ]);
}
