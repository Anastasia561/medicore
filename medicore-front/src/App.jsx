import {Routes, Route} from 'react-router-dom'
import Missing from "./pages/Missing.jsx";
import Providers from "./context/Providers.jsx";
import {PublicRoutes} from "./routes/PublicRoutes.jsx";
import {ProtectedRoutes} from "./routes/ProtectedRoutes.jsx";

function App() {

    return (
        <Providers>
            <Routes>
                {PublicRoutes}
                {ProtectedRoutes}
                <Route path="*" element={<Missing/>}/>
            </Routes>
        </Providers>
    )
}

export default App;
