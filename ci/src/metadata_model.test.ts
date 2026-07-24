import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { MetadataSchema } from "./metadata_model.ts";

describe("MetadataSchema", () => {
  it("parses valid metadata", () => {
    const metadata = MetadataSchema.parse({
      mod: { version: "1.2.3" },
    });
    assert.equal(metadata.mod.version, "1.2.3");
  });

  describe("throws for invalid data:", () => {
    it("missing mod table", () => {
      assert.throws(() => MetadataSchema.parse({}));
    });

    it("missing version", () => {
      assert.throws(() => MetadataSchema.parse({ mod: {} }));
    });

    it("empty version", () => {
      assert.throws(() => MetadataSchema.parse({ mod: { version: "" } }));
    });

    it("non-string version", () => {
      assert.throws(() => MetadataSchema.parse({ mod: { version: 123 } }));
    });
  });
});
