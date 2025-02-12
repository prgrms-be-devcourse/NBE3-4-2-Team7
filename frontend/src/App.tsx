// src/App.tsx
import React from "react";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import MainPage from "./app/travel/MainPage";
import TravelDetailPage from "./app/travel/TravelDetailPage";
import CreateTravelPage from "./app/travel/CreateTravelPage";

const App: React.FC = () => {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<MainPage/>}/>
                <Route path="/travels/:travelId" element={<TravelDetailPage/>}/>
                <Route path="/create" element={<CreateTravelPage/>}/>
                {/* 마이페이지 관련 라우터는 추후 추가 */}
            </Routes>
        </Router>
    );
};

export default App;
