// material-ui
import { Button, MenuItem, Paper, Select, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from '@mui/material';

// project imports
import MainCard from 'ui-component/cards/MainCard';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import dayjs from 'dayjs';
import * as React from 'react';
import axios from 'axios';
import { useEffect } from 'react';
import { useCookies } from 'react-cookie';

// ==============================|| SAMPLE PAGE ||============================== //

export default function PerformanceLogSearch() {
  const [fromPickerDate, setFromPickerDate] = React.useState(new Date());
  const [toPickerDate, setToPickerDate] = React.useState(new Date());
  const [followers, setFollowers] = React.useState([]);
  const [cookies, ,] = useCookies(['id', 'uid']);
  const [selectedFollower, setSelectedFollower] = React.useState(-1);
  const [resultLogs, setResultLogs] = React.useState([]);

  const getUid = () => {
    return cookies.uid;
  };

  console.log(toPickerDate);

  useEffect(() => {
    getFollowers();
  }, []);

  const getFollowers = async () => {
    console.log('getFollowers');
    axios.defaults.withCredentials = true;
    const urlStr = process.env.REACT_APP_API_URL + '/follower/followers?uid=' + getUid();
    try {
      const response = await axios.get(urlStr);
      setFollowers(response.data.followerList);
    } catch (e) {
      console.error(e);
      alert(e);
    }
  };

  const handleSelectChange = (event) => {
    setSelectedFollower(event.target.value);
  };

  const leftPad = (value) => {
    if (value >= 10) {
      return value;
    }
    return `0${value}`;
  };

  const toStringByFormatting = (source, delimiter = '-') => {
    const year = source.getFullYear();
    const month = leftPad(source.getMonth() + 1);
    const day = leftPad(source.getDate());

    return [year, month, day].join(delimiter);
  };

  const getFromDate = () => {
    return toStringByFormatting(fromPickerDate);
  };

  const getToDate = () => {
    return toStringByFormatting(toPickerDate);
  };

  const checkBoxReset = (perLogs) => {
    var tmpLogs = perLogs;
    tmpLogs.forEach((log) => {
      log.checked = false;
    });
    // setResultLogs(tmpLogs);
    return perLogs;
  };

  const handleDelete = async () => {
    let idArray = [];
    resultLogs.forEach((log) => {
      if (log.checked) {
        idArray.push(log.performanceLogId);
      }
    });
    axios.defaults.withCredentials = true;
    const urlStr = process.env.REACT_APP_API_URL + '/performance-log/delete-logs';
    const dataObj = {
      performanceLogIds: idArray,
      followerId: resultLogs && resultLogs[0].followerId,
      uid: getUid()
    };
    axios
      .post(urlStr, dataObj)
      .then((response) => {
        console.log(response);
        handleSearch();
      })
      .catch(function (error) {
        if (error?.data?.message == null) {
          alert(error);
        } else {
          alert(error.data.message);
        }
      });
  };

  const handleSearch = async () => {
    axios.defaults.withCredentials = true;
    const urlStr =
      process.env.REACT_APP_API_URL +
      '/performance-log/logs/' +
      getFromDate() +
      '/' +
      getToDate() +
      '/' +
      selectedFollower +
      '?uid=' +
      getUid();

    try {
      const response = await axios.get(urlStr);
      setResultLogs(checkBoxReset(response.data.performanceLogs));
      // alert(JSON.stringify(response.data.performanceLogs));
    } catch (e) {
      console.error(e);
      alert(e);
    }
  };

  return (
    <MainCard title="퍼포먼스 로그">
      <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="en">
        <Box sx={{ flexGrow: 1 }}>
          <Grid container spacing={1}>
            <Grid item xs={2}>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                label="Age"
                onChange={handleSelectChange}
                fullWidth
                size={'small'}
              >
                {followers &&
                  followers.map((v) => (
                    <MenuItem key={v.followerId} value={v.followerId}>
                      {v.nickName}
                    </MenuItem>
                  ))}
              </Select>
            </Grid>

            <Grid item xs={2} size="small">
              <DatePicker
                defaultValue={dayjs(new Date())}
                format="YYYY/MM/DD"
                value={dayjs(fromPickerDate)}
                onChange={(newValue) => {
                  setFromPickerDate(newValue.$d);
                  // getMemAndTasks(newValue.$d);
                }}
                slotProps={{ textField: { size: 'small' } }}
                fullwidth
                // value={getPickerDate()}
              />
            </Grid>
            <Grid item xs={2} size="small">
              <DatePicker
                defaultValue={dayjs(new Date())}
                format="YYYY/MM/DD"
                value={dayjs(toPickerDate)}
                onChange={(newValue) => {
                  setToPickerDate(newValue.$d);
                  // getMemAndTasks(newValue.$d);
                }}
                slotProps={{ textField: { size: 'small' } }}
                fullwidth
                // value={getPickerDate()}
              />
            </Grid>
            <Grid item xs={2}>
              <Button size="small" variant="contained" fullWidth onClick={handleSearch}>
                조회
              </Button>
              {/*<TextField fullWidth={true}></TextField>*/}
            </Grid>
            <Grid item xs={2}>
              <Button size="small" variant="contained" fullWidth onClick={handleDelete}>
                삭제
              </Button>
              {/*<TextField fullWidth={true}></TextField>*/}
            </Grid>
            <Grid item xs={2}></Grid>
            <Grid item xs={12}>
              구성원 : {resultLogs && resultLogs[0]?.followerName}
            </Grid>
            <TableContainer component={Paper}>
              <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
                <TableHead>
                  <TableRow>
                    <TableCell sx={{ width: '10%' }} align="center">
                      check
                    </TableCell>
                    <TableCell align="center" sx={{ width: '10%' }}>
                      ID
                    </TableCell>
                    <TableCell align="center" sx={{ width: '10%' }}>
                      날짜
                    </TableCell>
                    <TableCell align="center" sx={{ width: '80%' }}>
                      내용
                    </TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {resultLogs &&
                    resultLogs.map((log) => (
                      <TableRow key={log.performanceLogId} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                        <TableCell component="th" scope="row" sx={{ width: '10%' }} align="center">
                          <input
                            type="checkbox"
                            defaultChecked={log.checked}
                            onChange={(e) => {
                              log.checked = e.target.checked;
                            }}
                          ></input>
                        </TableCell>
                        <TableCell align="center" sx={{ width: '10%' }}>
                          {log.performanceLogId}
                        </TableCell>
                        <TableCell align="left">{log.date}</TableCell>
                        <TableCell align="left">{log.content}</TableCell>
                      </TableRow>
                    ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Grid>
        </Box>
      </LocalizationProvider>
    </MainCard>
  );
}
