import React, { useEffect } from "react";
import { Box, styled } from "@mui/material";
import images from "./constants/image";

const Container = styled(Box)({
  display: "flex",
  justifyContent: "center",
  alignItems: "center",
  width: "100vw", // 화면의 전체 너비
  height: "100vh", // 화면의 전체 높이
  transition: "background-color 0.3s ease",
});

const Logo = styled("img")({
  maxWidth: "30%",
  maxHeight: "30%",
  height: "auto",
  width: "auto",
  padding: "1.5em",
  willChange: "filter",
  transition: "filter 300ms",
  "&:hover": {
    filter: "drop-shadow(0 0 2em #646cffaa)",
  },
});

function App(): JSX.Element {
  const [state, setState] = React.useState<boolean>(true);

  useEffect(() => {
    const interval = setInterval(() => {
      setState((prevState) => !prevState);
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  return (
    <Container sx={{ backgroundColor: state ? "white" : "black" }}>
      <Logo
        src={state ? images.kokLogoWhite : images.kokLogoBlack}
        alt="Logo"
      />
    </Container>
  );
}

export default App;
