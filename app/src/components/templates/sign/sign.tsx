import M from "../../molecules/molecules.index";
import O from "../../organisms/organism.index";
import S from "./style";

function Sign(): JSX.Element {
  return (
    <S.Container>
      <M.SignImage />
      <O.SignBody />
      <M.TeamText />
    </S.Container>
  );
}
export default Sign;
