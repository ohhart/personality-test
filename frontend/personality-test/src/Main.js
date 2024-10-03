import React, { useContext } from 'react'

import Home from './components/Home'
import Result from './components/Result'
import Quiz from './components/Quiz'
import StateContext from './context/StateContext'

function Main(props) {

  const state = useContext(StateContext)

  if (state.quizExists) {
    return state.quizCompleted? <Result />: <Quiz />
  }

  return <Home />
}

export default Main