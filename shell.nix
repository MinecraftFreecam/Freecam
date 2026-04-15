{
  nixpkgs ? <nixpkgs>,
  system ? builtins.currentSystem,
  pkgs ? import nixpkgs { inherit system; },
}:
pkgs.callPackage (
  {
    mkShellNoCC,
    javaPackages,
    python3,
  }:
  mkShellNoCC {
    name = "freecam";
    project_dir = toString ./.;
    packages = [
      python3.pkgs.uv
      javaPackages.compiler.openjdk21
    ];
    shellHook = ''
      uv --directory "$project_dir" sync --locked
      source "$project_dir"/.venv/bin/activate
    '';
    __structuredAttrs = true;
  }
) { }
