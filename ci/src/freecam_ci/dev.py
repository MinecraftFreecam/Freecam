import os
import subprocess
import sys

from .project_files import CI

ANSI_ESC = "\x1b"
ANSI_COLOR_RESET = ANSI_ESC + "[39m"
ANSI_RED = ANSI_ESC + "[31m"
ANSI_BOLD = ANSI_ESC + "[1m"
ANSI_BOLD_RESET = ANSI_ESC + "[22m"
GHA = os.getenv("GITHUB_ACTIONS") == "true"


def error(msg: str):
    if GHA:
        msg = "::error::" + msg
    elif sys.stderr.isatty():
        msg = ANSI_BOLD + ANSI_RED + msg + ANSI_COLOR_RESET + ANSI_BOLD_RESET
    print(msg, file=sys.stderr)


def run(*cmd: str) -> int:
    print("+", " ".join(cmd), file=sys.stderr)
    result = subprocess.run(cmd)
    if result.returncode != 0:
        error(f"FAILED: exit code {result.returncode}")
    return result.returncode


def run_all(*cmds: list[str]) -> None:
    results = [run(*cmd) for cmd in cmds]
    errors = sum(result != 0 for result in results)
    if errors > 0:
        s = "s" if errors > 1 else ""
        error(f"{errors} error{s} occurred")
        sys.exit(1)


def check_python() -> None:
    run_all(
        ["ruff", "format", "--check"],
        ["ruff", "check"],
        ["mypy", str(CI)],
        ["pytest"],
    )


def format_python() -> None:
    status = run("ruff", "format")
    sys.exit(status)
