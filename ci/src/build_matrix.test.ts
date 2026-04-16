import { describe, it } from "node:test";
import assert from "node:assert/strict";
import path from "node:path";

import FIXTURES from "./fixtures.test.ts";
import {
  buildVersionMatrix,
  loadMatrixJobs,
  loadVersions,
} from "./build_matrix.ts";

describe("loadVersions", () => {
  it("valid", () => {
    const versions = loadVersions(
      "versions",
      path.resolve(FIXTURES, "valid_versions.toml"),
    );

    assert.equal(typeof versions, "object");

    for (const [key, value] of Object.entries(versions)) {
      assert.equal(typeof key, "string");
      assert.ok(
        value === null ||
          (Array.isArray(value) &&
            value.every((v) => typeof v === "string" || typeof v === "object")),
      );
    }
  });

  it("invalid", () => {
    assert.throws(() =>
      loadVersions("versions", path.resolve(FIXTURES, "invalid_versions.toml")),
    );
  });
});

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
    assert.equal(job120.upload?.name, "freecam-1.2.3-1.20");
  });
});

describe("loadMatrixJobs", () => {
  it("empty", () => {
    const matrix = loadMatrixJobs(
      "build",
      path.resolve(FIXTURES, "empty_matrix_jobs.toml"),
    );
    assert.deepEqual(matrix, []);
  });

  it("valid", () => {
    const matrix = loadMatrixJobs(
      "build",
      path.resolve(FIXTURES, "matrix_jobs.toml"),
    );

    assert.equal(matrix.length, 1);
    assert.equal(matrix[0]?.name, "test job 1");
  });

  it("invalid", () => {
    assert.throws(() =>
      loadMatrixJobs("build", path.resolve(FIXTURES, "bad_matrix_jobs.toml")),
    );
  });
});
