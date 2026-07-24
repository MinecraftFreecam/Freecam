import { describe, it } from "node:test";
import assert from "node:assert/strict";

import {
  SCProjectsByVersionSchema,
  SCProjectSlugSchema,
} from "./stonecutter_model.ts";
import path from "node:path";
import FIXTURES from "./fixtures.test.ts";
import TOML from "smol-toml";
import { readFileSync } from "node:fs";

describe("ProjectEntry", () => {
  it("string entries", () => {
    const entry = SCProjectSlugSchema.parse("core:1.20.1");
    assert.equal(entry.project, "core");
    assert.equal(entry.version, "1.20.1");
    assert.equal(entry.buildscript, null);
  });

  it("invalid", () => {
    assert.throws(() => SCProjectSlugSchema.parse(123));
    assert.throws(() => SCProjectSlugSchema.parse({ version: "1.0" }));
  });
});

describe("ProjectsByVersion", () => {
  it("valid", () => {
    const file = path.resolve(FIXTURES, "valid_versions.toml");
    const config = TOML.parse(readFileSync(file, "utf8"));
    const versions = SCProjectsByVersionSchema.parse(config.versions);

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
    const file = path.resolve(FIXTURES, "invalid_versions.toml");
    const config = TOML.parse(readFileSync(file, "utf8"));
    assert.throws(() => SCProjectsByVersionSchema.parse(config.versions));
  });
});
