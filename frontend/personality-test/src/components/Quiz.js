import React, { useContext, useEffect, useState } from 'react'
import Axios from 'axios'

import StateContext from '../context/StateContext'
import DispatchContext from '../context/DispatchContext'

function Quiz(props) {

  const state = useContext(StateContext)
  const dispatch = useContext(DispatchContext)

  const [currentQuestion, setCurrentQuestion] = useState({})
  const [selectedVariant, setSelectedVariant] = useState(0)

  async function loadQuiz() {
    try {
      const response = await Axios.get("http://localhost:8080/quizzes/" + state.quizId,
                                      {
                                        headers: {
                                          'Content-Type': 'application/json'
                                        }
                                      })

      setCurrentQuestion(response.data.currentQuestion)
    } catch(e) {
      console.log("Error loading quiz")
      console.log(e)
    }
  }

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

  async function updateVariant(e) {
    e.preventDefault()
    const variantId = e.target.getAttribute("data-variant-id")

    if (variantId != selectedVariant) {
      setSelectedVariant(variantId)
    } else {
      setSelectedVariant(0)
    }
  }

  async function answerQuestion() {
    const value = selectedVariant

    try {
      const response = await Axios.put("http://localhost:8080/quizzes/" + state.quizId,
                                      {
                                        "variant": + value
                                      },
                                      {
                                        headers: {
                                          'Content-Type': 'application/json'
                                        }
                                      })

      if (response.data.completed) {
        dispatch({type: "quizComplete", data: response.data.result})
      }
      setCurrentQuestion(response.data.currentQuestion)
      setSelectedVariant(0)
    } catch(e) {
      console.log("Error answering the quiz")
      console.log(e)
    }
  }



  useEffect(() => {
    loadQuiz()
  }, [])

  return (
    <div className="centered">
      <h1>Personality Test</h1>

      <div className="content center-left">
        {
          currentQuestion && currentQuestion.body && <p className="question">{currentQuestion.body}</p>
        }

        <div className="variants center-left">
          {
            currentQuestion && currentQuestion.variants && currentQuestion.variants.length > 0 &&
            <>
              {
                currentQuestion.variants.map(variant => {
                  return (
                    <div key={variant.placeInQuestion} className={variant.placeInQuestion == selectedVariant? "variant selected": "variant"} data-variant-id={variant.placeInQuestion} onClick={updateVariant}>
                      <div className="circle" data-variant-id={variant.placeInQuestion} onClick={updateVariant}></div>
                      <p data-variant-id={variant.placeInQuestion} onClick={updateVariant}>{variant.body}</p>
                    </div>
                  )
                })
              }
            </>
          }
        </div>

        <div className="actions">
          <div className="multiple-buttons">
            <div onClick={answerQuestion} className="btn primary">Answer</div>
            <div onClick={restartQuiz} className="btn secundary">Restart test</div>
          </div>
        </div>
      </div>
    
    </div>
  )
}

export default Quiz