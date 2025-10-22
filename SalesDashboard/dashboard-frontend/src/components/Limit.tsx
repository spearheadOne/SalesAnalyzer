import {useLimitStore} from "../store/limitStore.ts";

export default function Limit() {

    const {limits, limit, setLimit} = useLimitStore();

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
