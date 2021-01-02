from json import load, dumps
from copy import deepcopy
from random import choices, choice
import os
import asyncio
import websockets

subjects = load(open("subjects.json", "r"))
NUMBER_OF_SUBJECT = 25

MEANING = "Meaning"
READING = "Reading"


def printMeaning(subject):
    print("\nMeaning")
    for mean in subject["meanings"]:
        print(mean["meaning"])


def printReading(subject):
    print("\nReading")
    for mean in subject["readings"]:
        print(mean["reading"])


def printScore(score):
    print("Helene: {}".format(score["helene"]))
    print("Robin: Not counting score")
    print("Florent: {}".format(score["florent"]))


def computeUIState(state, question, show_answer):
    uistate = deepcopy(state)
    del uistate["score"]["robin"]
    del uistate["chosen_subjects"]

    subject = currentSubject(state)
    uistate["question"] = question
    uistate["kanji"] = subject["characters"]

    def standardizeAnswers(answers):
        return [{
            "characters": answer["reading"] if "reading" in answer else answer["meaning"],
            "primary": answer["primary"]
        } for answer in answers]

    def setAnswers(mainAnswers=None, additionalDetailsHeader=None, additionalDetails=None):
        uistate["main_answers"] = standardizeAnswers(mainAnswers) if mainAnswers is not None else None
        uistate["additionalDetails"] = {
            "header": additionalDetailsHeader,
            "body": standardizeAnswers(additionalDetails)
           } if additionalDetails is not None and additionalDetailsHeader is not None else None

    if show_answer:
        if question == MEANING:
            setAnswers(subject["meanings"], READING, subject["readings"])
        elif question == READING:
            setAnswers(subject["readings"], MEANING, subject["meanings"])
    else:
        setAnswers()

    return uistate


async def sendUIState(websocket, uistate):
    await websocket.send(dumps(uistate))


def currentSubject(state):
    return state["chosen_subjects"][state["pointer"]]


async def game(websocket, path):
    state = {
        "score": {
            "helene": 0,
            "florent": 0,
            "robin": 0
        },
        "chosen_subjects": choices(subjects, k=NUMBER_OF_SUBJECT),
        "pointer": 0,
    }
    while state["pointer"] < len(state["chosen_subjects"]):
        subject = currentSubject(state)
        # Show question
        question = choice([(MEANING, printMeaning, printReading),
                           (READING, printReading, printMeaning)])

        await sendUIState(websocket, computeUIState(state, question[0], False))

        os.system("clear")
        print("{}/{}".format(state["pointer"], NUMBER_OF_SUBJECT))
        printScore(state["score"])
        print()

        print(subject["characters"])
        print(question[0])
        input()

        # Show answer
        await sendUIState(websocket, computeUIState(state, question[0], True))
        print("Answer")
        question[1](subject)
        print()

        print("Additional information")
        question[2](subject)
        print()

        # Input winner
        winner = input("Who won (f/h):")

        if winner == 'h':
            state["score"]["helene"] = state["score"]["helene"] + 1
        elif winner == 'f':
            state["score"]["florent"] = state["score"]["florent"] + 1
        elif winner == 'r':
            state["score"]["robin"] = state["score"]["robin"] + 1

        state["pointer"] = state["pointer"] + 1

    with open("answer", "w") as f:
        f.write("Helene:{}, Florent: {}, Robin: {}".format(
            state["score"]["helene"], state["score"]["florent"],
            state["score"]["robin"]))


start_server = websockets.serve(game, "localhost", 8765)

asyncio.get_event_loop().run_until_complete(start_server)
asyncio.get_event_loop().run_forever()
