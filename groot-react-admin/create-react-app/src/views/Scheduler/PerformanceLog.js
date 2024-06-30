// material-ui
import { Button } from '@mui/material';

// project imports
import MainCard from 'ui-component/cards/MainCard';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import dayjs from 'dayjs';
import * as React from 'react';
import PerformEditor from './PerfomEditor';

// import * as cookies from '@babel/types';

// ==============================|| SAMPLE PAGE ||============================== //

export default function PerformanceLog() {
  const getInitDate = () => {
    return dayjs(Date());
  };
  const [pickerDate, setPickerDate] = React.useState(getInitDate()?.$d);

  // const getUid = () => {
  //   return cookies.uid;
  // };

  const todayPick = async () => {
    const newDate = dayjs(new Date());
    setPickerDate(newDate.$d);

    // console.log('todayPick');
    // axios.defaults.withCredentials = true;
    // const urlStr = process.env.REACT_APP_API_URL + '/follower/followers?uid=' + getUid();
    //
    // try {
    //   const response = await axios.get(urlStr);
    //   console.log(response);
    // } catch (e) {
    //   console.error(e);
    //   alert(e);
    // }
  };
  /*
  const previousDayPick = () => {
    const newDate = dayjs(pickerDate).subtract(1, 'day');
    setPickerDate(newDate.$d);
    getMemAndTasks(newDate.$d);
  };

  const nextDayPick = () => {
    const newDate = dayjs(pickerDate).add(1, 'day');
    setPickerDate(newDate.$d);
    getMemAndTasks(newDate.$d);
  };

  // nextDayPick 처럼 todayPick 도 만들어 줘.
  const todayPick = () => {
    const newDate = dayjs(new Date());
    setPickerDate(newDate.$d);
    getMemAndTasks(newDate.$d);
  };

 */
  const previousDayPick = () => {
    console.log('previousDayPick');
    const newDate = dayjs(pickerDate).subtract(1, 'day');
    setPickerDate(newDate.$d);
  };

  const nextDayPick = () => {
    console.log('nextDayPick');
    const newDate = dayjs(pickerDate).add(1, 'day');
    setPickerDate(newDate.$d);
  };

  return (
    <MainCard title="퍼포먼스 로그">
      <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="en">
        <Box sx={{ flexGrow: 1 }}>
          <Grid container spacing={1}>
            <Grid item xs={1}>
              <Button size="small" variant="contained" fullWidth onClick={todayPick}>
                오늘
              </Button>
              {/*<TextField fullWidth={true}></TextField>*/}
            </Grid>

            <Grid item xs={1}>
              <Button size="small" variant="contained" fullWidth onClick={previousDayPick}>
                전날
              </Button>
              {/*<TextField fullWidth={true}></TextField>*/}
            </Grid>
            <Grid item xs={2} size="small">
              <DatePicker
                defaultValue={dayjs(new Date())}
                format="YYYY/MM/DD"
                value={dayjs(pickerDate)}
                onChange={(newValue) => {
                  setPickerDate(newValue.$d);
                }}
                slotProps={{ textField: { size: 'small' } }}
                fullwidth
                // value={getPickerDate()}
              />
            </Grid>
            <Grid item xs={1}>
              <Button size="small" variant="contained" fullWidth onClick={nextDayPick}>
                다음날
              </Button>
              {/*<TextField fullWidth={true}></TextField>*/}
            </Grid>
            <Grid item xs={7}></Grid>
            <Grid item xs={12}>
              <PerformEditor pickerDate={pickerDate}></PerformEditor>
            </Grid>
          </Grid>
        </Box>
      </LocalizationProvider>
    </MainCard>
  );
}
