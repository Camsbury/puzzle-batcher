let
  inherit (import <nixpkgs> {}) fetchFromGitHub;
in
  import (fetchFromGitHub {
    owner = "NixOS";
    repo  = "nixpkgs";
    rev = "fd531dee22c9";
    hash = "sha256-AbScJXshYzbeUKHh+Y3OICc3iAtr+NqJ3Xb81GW+ptU=";
  })
