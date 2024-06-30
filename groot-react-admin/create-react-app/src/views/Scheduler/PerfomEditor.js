import Grid from '@mui/material/Grid';
import * as React from 'react';
import { Paper, Typography } from '@mui/material';
import Box from '@mui/material/Box';
import { CKEditor } from '@ckeditor/ckeditor5-react';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import { useEffect } from 'react';
import axios from 'axios';
import { useCookies } from 'react-cookie';
import PropTypes from 'prop-types';
// import dayjs from 'dayjs';

const PerformEditor = (props) => {
  const [cookies, ,] = useCookies(['id', 'uid']);
  const [logs, setLogs] = React.useState([]);

  const getUid = () => {
    return cookies.uid;
  };

  useEffect(() => {
    getFollowers();
  }, [props.pickerDate]);

  const mergeLogNFollowers = (followerList, logList) => {
    let tempLogList = [];

    followerList.forEach((follower) => {
      let isExist = false;
      logList.forEach((log) => {
        if (follower.followerId === log.followerId) {
          isExist = true;
          tempLogList = [...tempLogList, log];
        }
      });
      if (!isExist) {
        const tempLog = {
          performanceLogId: null,
          content: '',
          followerId: follower.followerId,
          followerName: follower.nickName
        };
        tempLogList = [...tempLogList, tempLog];
      }
    });
    tempLogList = [...tempLogList].sort((a, b) => {
      if (a['followerName'] > b['followerName']) return 1;
      if (a['followerName'] < b['followerName']) return -1;
      return 0;
    });
    setLogs(tempLogList);
  };

  const getLogs = async (followerList) => {
    console.log('getLogs');
    axios.defaults.withCredentials = true;
    const urlStr = process.env.REACT_APP_API_URL + '/performance-log/logs/' + toStringByFormatting(props.pickerDate) + '?uid=' + getUid();

    try {
      const response = await axios.get(urlStr);
      mergeLogNFollowers(followerList, response.data.performanceLogs);
    } catch (e) {
      console.error(e);
      alert(e);
    }
  };

  const getFollowers = async () => {
    console.log('getFollowers');
    setLogs([]);
    axios.defaults.withCredentials = true;
    const urlStr =
      process.env.REACT_APP_API_URL + '/follower/followers?uid=' + getUid() + '&date=' + toStringByFormatting(props.pickerDate);
    try {
      const response = await axios.get(urlStr, { withCredentials: true });
      getLogs(response.data.followerList);
    } catch (e) {
      console.error(e);
      alert(e);
    }
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

  function saveLog(logId, followerId, data) {
    // console.log(followerId + ':' + data);
    // return;
    axios.defaults.withCredentials = true;
    const urlStr = process.env.REACT_APP_API_URL + '/performance-log/save-log';

    const dataObj = {
      logId: logId,
      followerId: followerId,
      date: toStringByFormatting(props.pickerDate),
      log: data,
      uid: getUid()
    };

    axios
      .post(urlStr, dataObj)
      .then((response) => {
        console.log(response);
      })
      .catch(function (error) {
        if (error?.data?.message == null) {
          alert(error);
        } else {
          alert(error.data.message);
        }
      });
  }

  return (
    <>
      <Box sx={{ flexGrow: 1 }}>
        {logs &&
          logs.map((v) => (
            <Grid container spacing={1} key={v.performanceLogId} border={0}>
              <Grid item xs={2}>
                <Paper sx={{ height: '100%', p: 2, textAlign: 'center' }}>
                  <Typography>{v.followerName}</Typography>
                </Paper>
              </Grid>
              <Grid item xs={10}>
                <CKEditor
                  editor={ClassicEditor}
                  data={v.content}
                  onReady={(editor) => {
                    console.log('Editor is ready to use!', editor);
                  }}
                  onChange={() => {}}
                  onBlur={(event, editor) => {
                    saveLog(v.performanceLogId, v.followerId, editor.getData());
                  }}
                  onFocus={(event, editor) => {
                    console.log('Focus.', editor);
                  }}
                />
              </Grid>
            </Grid>
          ))}
      </Box>
    </>
  );
};

PerformEditor.propTypes = {
  pickerDate: PropTypes.instanceOf(Date).isRequired
};

export default PerformEditor;
