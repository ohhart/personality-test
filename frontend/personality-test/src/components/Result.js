import React, { useContext } from 'react'
import Axios from 'axios'

import StateContext from '../context/StateContext'
import DispatchContext from '../context/DispatchContext'

function Result(props) {

  const state = useContext(StateContext)
  const dispatch = useContext(DispatchContext)

  async function restartQuiz() {

    try {
      await Axios.delete("http://localhost:8080/quizzes/" + state.quizId,
                        {
                          headers: {
                            'Content-Type': 'application/json'
                          }
                        })
    } catch(e) {
      console.log("Error deleting quiz")
      console.log(e)
    }

    dispatch({type: "quizRestart"})
  }

  return (
    <div className="centered">
      <h1>Personality Test</h1>

      <div className="content">
        <p>The test is complete! You are an {state.quizResult}.</p>
      
        <div className="actions">
          <div onClick={restartQuiz} className="btn primary">Restart test</div>
        </div>
      </div>
    
    </div>
  );
}

export default Result