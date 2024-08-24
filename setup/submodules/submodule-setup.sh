#!/bin/bash

echo "프로젝트 루트 디렉토리로 이동 중..."
cd "$(git rev-parse --show-toplevel)" || { echo "프로젝트 루트 디렉토리로 이동 실패"; exit 1; }

echo "서브모듈 초기화 및 업데이트 중..."
git submodule update --init --recursive || { echo "서브모듈 초기화 실패"; exit 1; }

echo "서브모듈이 업데이트되었습니다."
