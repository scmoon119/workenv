import Grid from '@mui/material/Grid';
import { Button, IconButton, MenuItem, Select, TextField } from '@mui/material';
import * as React from 'react';
import NorthIcon from '@mui/icons-material/North';
import SouthIcon from '@mui/icons-material/South';
import { useEffect } from 'react';
import PropTypes from 'prop-types';

const TaskEditor = (props) => {
  const [id] = React.useState(props.task.id);
  const [importance, setImportance] = React.useState(props.task.importance);
  // const [priority] = React.useState(props.task.priority);
  const [taskStatus, setTaskStatus] = React.useState(props.task.taskStatus);
  const [text] = React.useState(props.task.text);
  const focusRef = React.useRef({});

  useEffect(() => {
    if (props.isFocused) focusRef.current.focus();
  }, [props.isFocused]);

  const taskStatusChange = (event) => {
    const taskStatus = event.target.value;
    let days = 1;
    let deligatedUser = '김씨';

    setTaskStatus(taskStatus); // 이 줄 필요해?

    if (taskStatus === '연기') {
      days = prompt('연기할 날수를 입력하세요', '1');
    }

    if (taskStatus === '위임') {
      deligatedUser = prompt('위임할 사람을 입력하세요', '서씨');
    }

    props.saveStatus(id, taskStatus, days, deligatedUser);
  };

  const updateText = (updatedText) => {
    if (updatedText == text) return;
    props.saveContent(id, updatedText);
  };

  const NorthDisabled = () => {
    if (getTextDisabled()) return true;
    return props.isNorthDisabled;
  };

  const SouthDisabled = () => {
    if (getTextDisabled()) return true;
    return props.isSouthDisabled;
  };

  const moveNorth = () => {
    props.moveNorth(id);
  };
  const moveSouth = () => {
    // alert(priority);
    props.moveSouth(id);
  };
  const onDeleteClick = () => {
    props.deleteTask(id);
  };

  const importanceChange = (event) => {
    const importance = event.target.value;
    setImportance(importance);
    props.saveImportance(id, importance);
  };

  const getTextDisabled = () => {
    if (taskStatus === '전' || taskStatus === '중') return false;
    else return true;
  };

  function handleEnterKey(event) {
    if (event.key === 'Enter') {
      updateText(event.target.value);
      props.addNextTask();
    }
  }

  return (
    <>
      <Grid item xs={1}>
        <Select
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          value={importance}
          label="Importance"
          onChange={importanceChange}
          fullWidth
          size={'small'}
        >
          <MenuItem value={'S'}>S</MenuItem>
          <MenuItem value={'A'}>A</MenuItem>
          <MenuItem value={'B'}>B</MenuItem>
          <MenuItem value={'C'}>C</MenuItem>
          <MenuItem value={'D'}>D</MenuItem>
        </Select>
      </Grid>
      <Grid item xs={0.5}>
        <IconButton
          disabled={NorthDisabled()}
          onClick={() => {
            moveNorth();
          }}
          size={'small'}
        >
          <NorthIcon> </NorthIcon>
        </IconButton>
      </Grid>
      <Grid item xs={0.5}>
        <IconButton
          disabled={SouthDisabled()}
          onClick={() => {
            moveSouth();
          }}
          size={'small'}
        >
          <SouthIcon></SouthIcon>
        </IconButton>
      </Grid>
      <Grid item xs={1}>
        {/*<InputLabel id="demo-simple-select-label">Age</InputLabel>*/}
        <Select
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          value={taskStatus}
          label="Age"
          onChange={taskStatusChange}
          fullWidth
          size={'small'}
        >
          <MenuItem value={'전'}>전</MenuItem>
          <MenuItem value={'중'}>중</MenuItem>
          <MenuItem value={'취소'}>취소</MenuItem>
          <MenuItem value={'완'}>완</MenuItem>
          <MenuItem value={'위임'}>위임</MenuItem>
          <MenuItem value={'연기'}>연기</MenuItem>
        </Select>
      </Grid>
      <Grid item xs={8}>
        <TextField
          ref={focusRef}
          fullWidth
          defaultValue={props.task.text}
          disabled={getTextDisabled()}
          style={{ textDecoration: getTextDisabled() ? 'line-through' : 'true' }}
          onBlur={(event) => {
            updateText(event.target.value);
          }}
          onKeyDown={handleEnterKey}
          size={'small'}
        ></TextField>
      </Grid>

      <Grid item xs={1}>
        <Button size="small" variant="contained" fullWidth onClick={onDeleteClick}>
          -
        </Button>
      </Grid>
    </>
  );
};

TaskEditor.propTypes = {
  task: PropTypes.shape({
    id: PropTypes.number.isRequired,
    importance: PropTypes.number.isRequired,
    priority: PropTypes.number.isRequired,
    taskStatus: PropTypes.string.isRequired,
    text: PropTypes.string.isRequired
  }).isRequired,
  isNorthDisabled: PropTypes.bool.isRequired,
  isSouthDisabled: PropTypes.bool.isRequired,
  moveNorth: PropTypes.func.isRequired,
  moveSouth: PropTypes.func.isRequired,
  saveContent: PropTypes.func.isRequired,
  saveImportance: PropTypes.func.isRequired,
  saveStatus: PropTypes.func.isRequired,
  deleteTask: PropTypes.func.isRequired,
  getLowestPriority: PropTypes.func.isRequired,
  addNextTask: PropTypes.func.isRequired,
  isFocused: PropTypes.bool.isRequired
};

export default TaskEditor;
