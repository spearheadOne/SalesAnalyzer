import './App.css'
import HistoricData from './components/HistoricData.tsx'
import LiveData from './components/LiveData.tsx'
import {useState} from "react";

function App() {

    const [active, setActive] = useState<'history' | 'live'>('live')

    //TODO: check if barchart can be unified
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
                {active === 'live' && <LiveData/>}
                {active === 'history' && <HistoricData/>}
            </div>
        </>
    )
}

export default App
