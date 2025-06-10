let
  pkgs = import ./pinned.nix {};
in
  with pkgs;
  mkShell {
    buildInputs = [
      clojure
      clj-kondo
      jdk
      nodejs
      yarn
    ];
  }
