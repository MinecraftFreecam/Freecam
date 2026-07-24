import { describe, it } from "node:test";
import assert from "node:assert/strict";

import {
  SCProjectsByVersionSchema,
  SCProjectSlugSchema,
} from "./stonecutter_model.ts";

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
    const config = {
      "1.21": ["common", "network"],
      "1.20": ["common", "core", "ui"],
      "1.19:1.19.1": ["common", "fabric", "leather"],
      "1.18:1.18.9:buildscript": ["common", "forge"],
    };

    const versions = SCProjectsByVersionSchema.parse(config);

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
    const config = {
      // Unrelated key
      foo: 123,
      // Missing `versions` key
    };

    assert.throws(() => SCProjectsByVersionSchema.parse(config));
  });
});
