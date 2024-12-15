import React, { useState } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

const DateRangePicker = ({ onDateChange }) => {
    const today = new Date();
    const [dateFrom, setDateFrom] = useState("2023-04-01");
    const [dateTo, setDateTo] = useState(today);

    const handleDateChange = (newDateFrom, newDateTo) => {
        setDateFrom(newDateFrom);
        setDateTo(newDateTo);
        if (onDateChange) {
            onDateChange({ dateFrom: newDateFrom, dateTo: newDateTo });
        }
    };

    return (
        <div>
            <div>
                <label>Vom </label>
                <DatePicker
                    selected={dateFrom}
                    onChange={(date) => handleDateChange(date, dateTo)}
                    dateFormat="yyyy-MM-dd"
                />
                <label> bis </label>
                <DatePicker
                    selected={dateTo}
                    onChange={(date) => handleDateChange(dateFrom, date)}
                    dateFormat="yyyy-MM-dd"
                />
            </div>
        </div>
    );
};

export default DateRangePicker;
