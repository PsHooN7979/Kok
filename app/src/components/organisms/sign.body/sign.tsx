import M from "../../molecules/molecules.index";
import S from "./style";

function SignBody(): JSX.Element {
  return (
    <S.SignBody>
      <M.SignField />
      <M.SignButton />
      <M.SignText />
    </S.SignBody>
  );
}
export default SignBody;
