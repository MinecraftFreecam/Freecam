import { describe, it } from "node:test";
import assert from "node:assert/strict";

import { buildVersionMatrix } from "./build_matrix.ts";

describe("buildVersionMatrix", () => {
  it("basic", () => {
    const versions = {
      "1.21": ["foo", "bar", "neoforge"],
      "1.20": ["common", "fabric", "forge"],
    };

    const matrix = buildVersionMatrix("1.2.3", versions);

    assert.equal(matrix.length, 2);

    const names = matrix.map((job) => job.name);
    assert.ok(names.includes("MC 1.20"));
    assert.ok(names.includes("MC 1.21"));

    const job121 = matrix.find((j) => j.name === "MC 1.21");
    assert.ok(job121);
    assert.deepEqual(job121.gradle_args, [":neoforge:1.21:buildAndCollect"]);

    const job120 = matrix.find((j) => j.name === "MC 1.20");
    assert.ok(job120);
    assert.deepEqual(job120.gradle_args, [
      ":fabric:1.20:buildAndCollect",
      ":forge:1.20:buildAndCollect",
    ]);
    assert.equal(job120.upload?.name, "mc-1.20");
  });
});
