import {useLimitStore} from "../store/limitStore.ts";

export default function Limit() {

    const { limits, limit, setLimit } = useLimitStore();

    return (
        <div className="d-flex align-items-center gap-2">
            <div className="dropdown">
                <button className="btn btn-secondary btn-sm dropdown-toggle"
                        type="button"
                        id="dropdownMenuButton1"
                        data-bs-toggle="dropdown"
                        aria-expanded="false">
                    {limit}
                </button>
                <ul className="dropdown-menu">
                    {limits.map((l) => (
                        <li key={l}>
                            <a className="dropdown-item" href="#"
                               onClick={() => setLimit(l)}>
                                {l}
                            </a>
                        </li>
                    ))}
                </ul>
            </div>
            <span>Records</span>
        </div>
    )
}
