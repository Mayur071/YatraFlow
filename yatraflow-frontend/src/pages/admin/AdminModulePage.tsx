
interface AdminModulePageProps {
    title: string;
    description: string;
}

const AdminModulePage = ({ title, description }: AdminModulePageProps) => {
    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-bold tracking-tight text-gray-900">
                    {title}
                </h1>

                <p className="mt-2 text-sm text-gray-500">
                    {description}
                </p>
            </div>

            <div className="rounded-xl border border-gray-200 bg-white p-8 shadow-sm">
                <div className="flex min-h-64 flex-col items-center justify-center text-center">
                    <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-blue-50 text-blue-600">
                        <span className="text-2xl">⚙</span>
                    </div>

                    <h2 className="text-lg font-semibold text-gray-900">
                        {title} Management
                    </h2>

                    <p className="mt-2 max-w-md text-sm leading-6 text-gray-500">
                        This module is being prepared. Management features,
                        data tables, forms and backend integration will
                        be available here.
                    </p>
                </div>
            </div>
        </div>
    );
};

export default AdminModulePage;
