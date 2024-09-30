import React from "react";
import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import App from "./App";

describe("App component", () => {
  it("renders Hello, Vitest! text", () => {
    render(<App />);
    expect(screen.getByText(/hello, vitest!/i)).toBeInTheDocument();
  });
});
