import { describe, it } from "node:test";
import assert from "node:assert/strict";

import { MatrixJobSchema } from "./matrix_model.ts";

describe("MatrixJob / MatrixUpload", () => {
  it("valid with upload", () => {
    const job = MatrixJobSchema.parse({
      name: "Build thing",
      gradle_args: [":common:thing"],
      upload: {
        path: "build/libs/*.jar",
        name: "artifact",
      },
    });

    assert.deepEqual(job, {
      name: "Build thing",
      gradle_args: [":common:thing"],
      upload: {
        path: "build/libs/*.jar",
        name: "artifact",
        days: 90,
        archive: true,
      },
    });
  });

  it("no archive", () => {
    const job = MatrixJobSchema.parse({
      name: "Build file",
      gradle_args: [":common:file"],
      upload: {
        path: "build/libs/*.jar",
        archive: false,
      },
    });

    assert.deepEqual(job.upload, {
      path: "build/libs/*.jar",
      days: 90,
      archive: false,
    });
  });

  it("no upload", () => {
    const job = MatrixJobSchema.parse({
      name: "Build test",
      gradle_args: [":common:test"],
    });

    assert.equal(job.upload, undefined);
  });

  it("invalid upload", () => {
    assert.throws(() =>
      MatrixJobSchema.parse({
        name: "Build test",
        gradle_args: [":common:test"],
        upload: {},
      }),
    );
  });
});
