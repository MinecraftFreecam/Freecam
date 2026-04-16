import { describe, it } from "node:test";
import assert from "node:assert/strict";
import path from "node:path";
import FIXTURES from "./fixtures.test.ts";
import { MetadataError, readVersion } from "./read_version.ts";

describe("read_version", () => {
  it("readVersion reads real project version", () => {
    const version = readVersion();
    assert.ok(version);
    // noinspection SuspiciousTypeOfGuard
    assert.ok(typeof version === "string");
    assert.notEqual(version, "");
  });

  it("readVersion reads valid_version", () => {
    const fixture = path.resolve(FIXTURES, "valid_version.toml");
    const version = readVersion(fixture);
    assert.equal(version, "1.2.3");
  });

  describe("readVersion throws for:", () => {
    const fixtures = [
      { name: "missing file", filename: "missing_file.toml" },
      { name: "missing version", filename: "missing_version.toml" },
      { name: "missing mod table", filename: "missing_mod_table.toml" },
      { name: "empty version", filename: "empty_version.toml" },
      { name: "non-string version", filename: "non_str_version.toml" },
    ];
    for (const { name, filename } of fixtures) {
      it(name, () => {
        const fixture = path.resolve(FIXTURES, filename);
        assert.throws(() => readVersion(fixture), MetadataError);
      });
    }
  });
});
