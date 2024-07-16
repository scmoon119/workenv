import * as React from 'react';
import MainCard from 'ui-component/cards/MainCard';
import Grid from '@mui/material/Grid';

import Box from '@mui/material/Box';
import { Button } from '@mui/material';

import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';

import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';
import TaskEditor from './TaskEditor';
import { useEffect, useRef } from 'react';

import { CKEditor } from '@ckeditor/ckeditor5-react';
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';
import axios from 'axios';
import { useCookies } from 'react-cookie';

import AddIcon from '@mui/icons-material/Add';
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import CalendarTodayIcon from '@mui/icons-material/CalendarToday';
import NavigateBeforeIcon from '@mui/icons-material/NavigateBefore';
export default function Scheduler() {
  const [tasks, setTasks] = React.useState([]);
  const [memo, setMemo] = React.useState('');
  const [unSaved, setUnSaved] = React.useState(false);
  const [cookies, ,] = useCookies(['uid']);
  const [createdTaskId, setCreatedTaskId] = React.useState(-1);
  // const [refs, setRefs] = React.useState([]);

  const getInitDate = () => {
    return dayjs(Date());
  };

  const [pickerDate, setPickerDate] = React.useState(getInitDate()?.$d);

  const getUid = () => {
    return cookies.uid;
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

  const isDoneStatus = (str) => {
    return str === '전' || str === '중';
  };

  const isUnDoneStatus = (str) => {
    return str === '완' || str === '취소' || str === '위임' || str === '연기';
  };

  const getModifiedTask = (task) => {
    const t = {
      id: task.taskId,
      importance: task.importance,
      priority: task.priority,
      taskStatus: task.status,
      text: task.content
    };
    return t;
  };
  const setMemoAndTasks = (data) => {
    if (data?.message == 'OK') {
      setMemo(data?.memo?.content != null ? data?.memo?.content : '');
      let taskList = [];

      for (const task of data.taskList.filter((t) => isDoneStatus(t.status))) {
        taskList = [...taskList, getModifiedTask(task)];
      }
      for (const task of data.taskList.filter((t) => isUnDoneStatus(t.status))) {
        taskList = [...taskList, getModifiedTask(task)];
      }

      setTasks(taskList);
      // eslint-disable-next-line react-hooks/rules-of-hooks
      // 각 TaskEditor에 대한 참조 배열 생성
    } else {
      alert(data?.message);
    }
  };

  const getMemAndTasks = async (date) => {
    // console.log('getMemAndTasks:date ' + toStringByFormatting(date));
    // axios.defaults.withCredentials = true;
    const urlStr = process.env.REACT_APP_API_URL + '/task/tasks?date=' + toStringByFormatting(date) + '&uid=' + getUid();
    try {
      const response = await axios.get(urlStr, { withCredentials: true });
      setMemoAndTasks(response.data);
    } catch (e) {
      console.error(e);
      alert(e);
    }
  };

  useEffect(() => {
    getMemAndTasks(getInitDate()?.$d);
    console.log(refs);
    return () => {};
  }, []);

  const getLowestPriority = (importance) => {
    let lowestPriority = -1;
    tasks.map((v) => {
      if (v.importance === importance) {
        if (lowestPriority < v.priority) {
          lowestPriority = v.priority;
        }
      }
    });
    return lowestPriority + 1;
  };

  const isMinPriority = (priority, importance) => {
    let minPriority = 10000;
    tasks.map((v) => {
      if (v.importance === importance) {
        if (minPriority > v.priority) {
          minPriority = v.priority;
        }
      }
    });
    return minPriority == priority;
  };

  const isNorthDisabled = (task) => {
    if (isDoneStatus(task.status)) {
      return true;
    }
    // return false;
    return isMinPriority(task.priority, task.importance);
    // if (task.priority == 0) {
    //   return true;
    // }
    // return false;
  };

  const isSouthDisabled = (task) => {
    if (isDoneStatus(task.status)) {
      return true;
    }

    let result = isMaxPriority(task.priority, task.importance);
    // console.log('isSouthDisabled', getMaxPriority(task.importance), result, task.priority, task.importance, task.text);
    return result;
    // return isMaxPriority(task.priority, task.importance);

    // tasks.map((v) => {
    //   if (v.importance === task.importance && v.priority >= ENABLED_PRIORITY_MAX) {
    //     return true;
    //   }
    // });
    // return isMinPriority(task.priority, task.importance);
  };

  // const getMaxPriority = (importance) => {
  //   let maxPriority = 0;
  //   tasks.map((v) => {
  //     if (v.importance === importance) {
  //       if (maxPriority < v.priority && v.priority < 10000) {
  //         maxPriority = v.priority;
  //       }
  //     }
  //   });
  //   return maxPriority;
  // };

  const isMaxPriority = (priority, importance) => {
    let maxPriority = 0;
    tasks.map((v) => {
      if (v.importance === importance) {
        if (maxPriority < v.priority && v.priority < 10000) {
          maxPriority = v.priority;
        }
      }
    });
    return maxPriority == priority;
  };

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

  const plusClick = () => {
    axios.defaults.withCredentials = true;
    axios
      .put(process.env.REACT_APP_API_URL + '/task/task', {
        date: toStringByFormatting(pickerDate),
        uid: getUid()
      })
      .then((response) => {
        if (response.data.message != 'OK') {
          alert(response.data?.message);
        } else {
          // 만약 response.data?.taskId 가 null 이 아니면 setCreatedTaskId(response.data?.taskId); 호출
          if (response.data?.taskId != null) {
            setCreatedTaskId(response.data?.taskId);
          }
        }
        getMemAndTasks(pickerDate, createdTaskId);
      })
      .catch(function (error) {
        if (error?.data?.message == null) {
          alert(error);
        } else {
          alert(error.data.message);
        }
      });
  };

  const deleteTaskByScheduler = (id) => {
    console.log(id);
    const url = process.env.REACT_APP_API_URL + '/task/task/' + id + '/' + getUid();
    axios.defaults.withCredentials = true;
    axios
      .delete(url)
      .then((response) => {
        console.log(response);
        getMemAndTasks(pickerDate);
      })
      .catch(function (error) {
        console.log(error);
        if (error?.data?.message == null) {
          alert(error);
        } else {
          alert(error.data.message);
        }
      });
  };

  const saveMemo = (memoContent) => {
    console.log(memoContent);
    axios
      .post(process.env.REACT_APP_API_URL + '/memo/memo', {
        date: toStringByFormatting(pickerDate),
        content: memoContent,
        uid: getUid()
      })
      .then((response) => {
        console.log(response);
        setUnSaved(true);
      })
      .catch(function (error) {
        if (error?.data?.message == null) {
          alert(error);
        } else {
          alert(error.data.message);
        }
      });
  };

  const saveStatusByScheduler = (id, taskStatus, days, deligatedUser) => {
    console.log(id, ' : ', taskStatus, ' : ', days);

    if (deligatedUser === null) {
      deligatedUser = '';
    }
    axios
      .post(process.env.REACT_APP_API_URL + '/task/taskByStatus', {
        id: id,
        status: taskStatus,
        dayToPostpone: days,
        deligatedUser: deligatedUser,
        uid: getUid()
      })
      .then((response) => {
        console.log(response);
        getMemAndTasks(pickerDate);
      })
      .catch(function (error) {
        if (error?.data?.message == null) {
          alert(error);
        } else {
          alert(error.data.message);
        }
      });
  };
  const saveImportanceByScheduler = (id, importance) => {
    console.log(id, ' : ', importance);

    axios
      .post(process.env.REACT_APP_API_URL + '/task/taskByImportance', {
        id: id,
        importance: importance,
        uid: getUid()
      })
      .then((response) => {
        console.log(response);
        getMemAndTasks(pickerDate);
      })
      .catch(function (error) {
        if (error?.data?.message == null) {
          alert(error);
        } else {
          alert(error.data.message);
        }
      });
  };
  const saveContentByScheduler = (id, content) => {
    console.log(id, ' : ', content);

    setCreatedTaskId(-1);
    axios
      .post(process.env.REACT_APP_API_URL + '/task/taskByContent', {
        id: id,
        content: content,
        uid: getUid()
      })
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
  };

  const moveNorthBySch = (id) => {
    console.log(id);
    // process.env.REACT_APP_API_URL + '/moveNorth' , id body 로 post  호출하고 완료되면 getMemAndTasks 호출
    axios
      .post(process.env.REACT_APP_API_URL + '/task/moveNorth', {
        id: id,
        uid: getUid()
      })
      .then((response) => {
        console.log(response);
        getMemAndTasks(pickerDate);
      })
      .catch(function (error) {
        if (error?.data?.message == null) {
          alert(error);
        } else {
          alert(error.data.message);
        }
      });
  };
  const moveSouthBySch = (id) => {
    console.log(id);
    // moveNorth 와 똑같이 이름만 moveSouth 로 해줘
    axios
      .post(process.env.REACT_APP_API_URL + '/task/moveSouth', {
        id: id,
        uid: getUid()
      })
      .then((response) => {
        console.log(response);
        getMemAndTasks(pickerDate);
      })
      .catch(function (error) {
        if (error?.data?.message == null) {
          alert(error);
        } else {
          alert(error.data.message);
        }
      });
  };

  const refs = useRef(tasks.map(() => React.createRef())); // 각 TaskEditor에 대한 참조 배열 생성

  return (
    <MainCard title="to do list">
      <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="en">
        <Box sx={{ flexGrow: 1 }}>
          <Grid container spacing={1}>
            <Grid item xs={1}>
              <Button size="small" variant="contained" fullWidth onClick={plusClick}>
                <AddIcon />
              </Button>
              {/*<TextField fullWidth={true}></TextField>*/}
            </Grid>
            <Grid item xs={1}>
              <Button size="small" variant="contained" fullWidth onClick={todayPick}>
                <CalendarTodayIcon />
              </Button>
              {/*<TextField fullWidth={true}></TextField>*/}
            </Grid>
            <Grid item xs={1}>
              <Button size="small" variant="contained" fullWidth onClick={previousDayPick}>
                <NavigateBeforeIcon />
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
                  getMemAndTasks(newValue.$d);
                }}
                slotProps={{ textField: { size: 'small' } }}
                fullwidth
                // value={getPickerDate()}
              />
            </Grid>
            <Grid item xs={1}>
              <Button size="small" variant="contained" fullWidth onClick={nextDayPick}>
                <NavigateNextIcon />
              </Button>
              {/*<TextField fullWidth={true}></TextField>*/}
            </Grid>
            <Grid item xs={6}></Grid>
            {tasks.map((v, index) => {
              // eslint-disable-next-line react-hooks/rules-of-hooks
              return (
                <TaskEditor
                  key={v.id}
                  task={v}
                  isNorthDisabled={isNorthDisabled(v)}
                  isSouthDisabled={isSouthDisabled(v)}
                  moveNorth={moveNorthBySch.bind(this)}
                  moveSouth={moveSouthBySch.bind(this)}
                  saveContent={saveContentByScheduler.bind(this)}
                  saveImportance={saveImportanceByScheduler.bind(this)}
                  saveStatus={saveStatusByScheduler.bind(this)}
                  deleteTask={deleteTaskByScheduler.bind(this)}
                  getLowestPriority={getLowestPriority.bind(this)}
                  addNextTask={plusClick.bind(this)}
                  isFocused={createdTaskId === v.id}
                  subCreatedTaskId={createdTaskId}
                  nextTaskRef={index < tasks.length - 1 ? refs.current[index + 1] : null} // 다음 TaskEditor에 대한 참조 전달
                  prevTaskRef={index > 0 ? refs.current[index - 1] : null} // 이전 TaskEditor에 대한 참조 전달
                  thisRef={refs.current[index]} // 현재 TaskEditor에 대한 참조 전달
                />
              );
            })}{' '}
            <Grid item xs={12}>
              <div hidden={unSaved}> * </div>
              <CKEditor
                editor={ClassicEditor}
                data={memo}
                onReady={(editor) => {
                  console.log('Editor is ready to use!', editor);
                }}
                onChange={() => {
                  if (unSaved) {
                    setUnSaved(false);
                  }
                }}
                onBlur={(event, editor) => {
                  console.log('Blur.', encodeURIComponent(editor.getData()));
                  saveMemo(editor.getData());
                }}
                onFocus={(event, editor) => {
                  console.log('Focus.', editor);
                }}
              />
            </Grid>
          </Grid>
        </Box>
      </LocalizationProvider>
    </MainCard>
  );
}
