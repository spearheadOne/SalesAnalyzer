import {type LimitState} from "../store/limitState.ts";
import type {StoreApi, UseBoundStore} from "zustand";

export default function Limit({limitStore}: { limitStore: UseBoundStore<StoreApi<LimitState>> }) {

    const state = limitStore(s => ({
        limits: s.limits,
        limit: s.limit,
        setLimit: s.setLimit,
    }))

    const {limits, limit, setLimit} = state

    return (
        <div className="d-flex align-items-center gap-2">
            <span>Records: </span>
            <div className="dropdown">
                <button className="btn btn-secondary btn-sm dropdown-toggle"
                        type="button"
                        id="dropdownMenuButton1"
                        data-bs-toggle="dropdown"
                        aria-haspopup="true"
                        aria-controls="limitMenu"
                        aria-expanded="false">
                    {limit}
                </button>
                <ul className="dropdown-menu" aria-labelledby="limitDropdown" role="menu">
                    {limits.map((l) => (
                        <li key={l}>
                            <button type="button" className="dropdown-item" role="menuitem"
                                    onClick={() => setLimit(l)}>
                                {l}
                            </button>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    )
}
