#!/bin/bash
#주의점! 해당 스크립트는 LF 로 해석되어야 합니다. ( 인텔리제이에서 CLRF 로 설정되어 있는 경우 변경요망)

echo "프로젝트 루트 디렉토리로 이동 중..."
cd "$(git rev-parse --show-toplevel)" || { echo "프로젝트 루트 디렉토리로 이동 실패"; exit 1; }

echo "서브모듈 초기화 및 업데이트 중..."
git submodule update --init --recursive || { echo "서브모듈 초기화 실패"; exit 1; }

echo "서브모듈이 업데이트되었습니다."
