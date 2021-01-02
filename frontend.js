class MyFrontend extends React.Component{
	constructor(props){
		super(props)
		this.state = {}
	}

	renderAnswers(answers, className){
		if (answers != null){
			return <ul className={className}>{answers.map(answer => <li key={answer.characters}>{answer.characters}</li>)}</ul>
		} else{
			return <div className={className}></div>
		}
	}

	score(name, score){
		return <div className="score"><p className="name">{name}</p><p>{score}</p></div>
	}

	render(){
		if (_.isEmpty(this.state)){
			return <p>Loading</p>
		}

		let main_answers = this.renderAnswers(this.state.main_answers, "mainAnswers")
		let additional_information = this.state.additionalDetails != null ?
			<div className="additionalDetails"><h3>{this.state.additionalDetails.header}</h3>{this.renderAnswers(this.state.additionalDetails.body)}</div>:<div className="additionalDetails"></div>

		return <div className="container">
						 <h1>{this.state.kanji}</h1>
						 <h2 className="question">{this.state.pointer}: {this.state.question}</h2>
						 {main_answers}
						 {additional_information}
						 <h2 className="scoreTitle">Scores</h2>
						 {this.score("Helene", this.state.score.helene)}
						 {this.score("Robin", "Not keeping score")}
						 {this.score("Florent", this.state.score.florent)}
					 </div>
	}

	componentDidMount(){
		const socket = new WebSocket("ws://127.0.0.1:8765")
		socket.addEventListener('message', e => {
			this.setState(JSON.parse(e.data))
		})
	}

}

ReactDOM.render(<MyFrontend/>, document.body);
