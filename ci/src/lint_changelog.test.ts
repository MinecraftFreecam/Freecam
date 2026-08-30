import { describe, it } from "node:test";
import assert from "node:assert/strict";
import path from "node:path";

import FIXTURES from "./fixtures.test.ts";
import { lint, LintError } from "./lint_changelog.ts";

describe("lint_changelog", () => {
  it("lint accepts valid changelog", () => {
    const fixture = path.resolve(FIXTURES, "ok_current_version.md");
    assert.doesNotThrow(() => lint("1.2.3", fixture));
  });

  describe("lint throws for:", () => {
    const fixtures = [
      { name: "missing release", filename: "missing_release.md" },
      { name: "has unreleased section", filename: "has_unreleased.md" },
      { name: "uses old format", filename: "old_format.md" },
    ];
    for (const { name, filename } of fixtures) {
      it(name, () => {
        const fixture = path.resolve(FIXTURES, filename);
        assert.throws(() => lint("1.2.3", fixture), LintError);
      });
    }
  });
});
