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
  }:
  mkShellNoCC {
    name = "freecam";
    project_dir = toString ./.;
    packages = [
      javaPackages.compiler.openjdk21
      nodejs_24
    ];
    shellHook = ''
      ( cd "$project_dir" && npm ci )
      export PATH="$project_dir/node_modules/.bin:$PATH"
    '';
    __structuredAttrs = true;
  }
) { }
