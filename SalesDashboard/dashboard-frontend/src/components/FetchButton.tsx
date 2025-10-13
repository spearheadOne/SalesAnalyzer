interface FetchButtonProps {
    fetchData: () => void | Promise<void>;
}


export default function FetchButton({fetchData}: FetchButtonProps) {

    const handleClick = async () => {
        await fetchData();

    }

    return (
        <button className="btn btn-primary"
                type="submit"
                onClick={handleClick}>
            Get data
        </button>
    )
}