import { describe, it } from "node:test";
import assert from "node:assert/strict";

import { SCProjectSlugSchema } from "./stonecutter_model.ts";

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
