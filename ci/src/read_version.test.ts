import { describe, it } from "node:test";
import assert from "node:assert/strict";
import path from "node:path";
import FIXTURES from "./fixtures.test.ts";
import { MetadataError, readVersion } from "./read_version.ts";

describe("readVersion", () => {
  it("reads real project version", () => {
    const version = readVersion();
    assert.ok(version);
    // noinspection SuspiciousTypeOfGuard
    assert.ok(typeof version === "string");
    assert.notEqual(version, "");
  });

  it("reads valid_version fixture", () => {
    const fixture = path.resolve(FIXTURES, "valid_version.toml");
    const version = readVersion(fixture);
    assert.equal(version, "1.2.3");
  });

  it("throws MetadataError for missing file", () => {
    const fixture = path.resolve(FIXTURES, "missing_file.toml");
    assert.throws(() => readVersion(fixture), MetadataError);
  });
});
