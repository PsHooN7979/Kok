// vitest.config.js
export default {
  test: {
    globals: true, // 글로벌 expect와 describe를 활성화
    environment: "jsdom", // React 컴포넌트 테스트를 위한 jsdom 환경 설정
  },
};
