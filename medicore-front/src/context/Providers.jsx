import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {Toaster} from 'react-hot-toast';
import {toastOptions} from "./toastOptions.js";
import {AuthContext} from "./AuthContext.jsx";

const queryClient = new QueryClient();

const Providers = ({children}) => {
    return (
        <AuthContext>
            <QueryClientProvider client={queryClient}>
                <Toaster {...toastOptions} />
                {children}
            </QueryClientProvider>
        </AuthContext>
    );
};

export default Providers;