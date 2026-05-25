import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {Toaster} from 'react-hot-toast';
import {toastOptions} from "./toastOptions.js";
import {AuthProvider} from "./AuthProvider.jsx";

const queryClient = new QueryClient();

const Providers = ({children}) => {
    return (
        <AuthProvider>
            <QueryClientProvider client={queryClient}>
                <Toaster {...toastOptions} />
                {children}
            </QueryClientProvider>
        </AuthProvider>
    );
};

export default Providers;