import { useImmerReducer } from "use-immer"

import './App.css';
import Main from "./Main";
import StateContext from "./context/StateContext";
import DispatchContext from "./context/DispatchContext";


function App() {

  const initialState = {
    quizExists: Boolean(localStorage.getItem("quizId")),
    quizCompleted: Boolean(localStorage.getItem("quizResult")),
    quizId: localStorage.getItem("quizId"),
    quizResult: localStorage.getItem("quizResult")
  }

  function reducer(draft, action) {
    switch(action.type) {
      case "quizStart": 
        draft.quizExists = true
        draft.quizId = action.data
        localStorage.setItem("quizId", action.data)
        return
      case "quizRestart":
        draft.quizExists = false
        draft.quizCompleted = false
        draft.quizId = ""
        draft.quizResult = ""
        localStorage.removeItem("quizId")
        localStorage.removeItem("quizResult")
        return
      case "quizComplete":
        draft.quizCompleted = true
        draft.quizResult = action.data
        localStorage.setItem("quizResult", action.data)
    }
  }

  const [state, dispatch] = useImmerReducer(reducer, initialState)

  return (
    <StateContext.Provider value={state}>
      <DispatchContext.Provider value={dispatch}>
        <Main />
      </DispatchContext.Provider>
    </StateContext.Provider>
  );
}

export default App;
