import { describe, it } from "node:test";
import assert from "node:assert/strict";

import { MatrixJobSchema, MatrixJobsFileSchema } from "./matrix_model.ts";

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

describe("MatrixJobsFileSchema", () => {
  it("defaults to empty array when undefined", () => {
    const matrix = MatrixJobsFileSchema.parse({});
    assert.deepEqual(matrix, { builds: [] });
  });

  it("valid array of jobs", () => {
    const matrix = MatrixJobsFileSchema.parse({
      build: [
        {
          name: "test job 1",
          gradle_args: [":test"],
        },
      ],
    });

    assert.equal(matrix.builds.length, 1);
    assert.equal(matrix.builds[0]?.name, "test job 1");
  });

  it("invalid structure", () => {
    assert.throws(() =>
      MatrixJobsFileSchema.parse({
        build: [{ invalid: "data" }],
      }),
    );
  });
});
