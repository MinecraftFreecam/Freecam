{
  nixpkgs ? <nixpkgs>,
  system ? builtins.currentSystem,
  pkgs ? import nixpkgs { inherit system; },
}:
pkgs.callPackage (
  {
    mkShellNoCC,
    javaPackages,
    nodejs_24,
    python3,
  }:
  mkShellNoCC {
    name = "freecam";
    project_dir = toString ./.;
    packages = [
      python3.pkgs.uv
      javaPackages.compiler.openjdk21
      nodejs_24
    ];
    shellHook = ''
      uv --directory "$project_dir" sync --locked
      source "$project_dir"/.venv/bin/activate
      ( cd "$project_dir" && npm ci )
      export PATH="$project_dir/node_modules/.bin:$PATH"
    '';
    __structuredAttrs = true;
  }
) { }
