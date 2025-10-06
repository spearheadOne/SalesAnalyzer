import './App.css'
import History from './components/History'
import Live from './components/Live.tsx'
import {useState} from "react";

function App() {

    const [active, setActive] = useState<'history' | 'live'>('live')

    return (
        <>
            <nav className="navbar navbar-expand-lg bg-body-tertiary">
                <div className="container-fluid">
                    <span className="navbar-brand">Sales data</span>
                    <div className="collapse navbar-collapse" id="navbarNavAltMarkup">
                        <div className="navbar-nav">
                            <button className="nav-link btn btn-link" onClick={() => setActive('live')}>
                                Live data
                            </button>
                            <button className="nav-link btn btn-link" onClick={() => setActive('history')}>
                                Historic data
                            </button>
                        </div>
                    </div>
                </div>
            </nav>

            <div className="container">
                {active === 'live' && <Live/>}
                {active === 'history' && <History/>}
            </div>
        </>
    )
}

export default App
