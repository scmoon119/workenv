// material-ui
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField
} from '@mui/material';

// project imports
import MainCard from 'ui-component/cards/MainCard';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import * as React from 'react';
import RefreshIcon from '@mui/icons-material/Refresh';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import { useEffect } from 'react';
// import axios from 'axios';
import { useCookies } from 'react-cookie';
import axios from 'axios';

// ==============================|| SAMPLE PAGE ||============================== //

export default function FollwerManager() {
  const [cookies, ,] = useCookies(['id', 'uid']);
  const [followers, setFollowers] = React.useState([]);
  const [openAdd, setOpenAdd] = React.useState(false);
  const [openModify, setOpenModify] = React.useState(false);
  const [modifyFollowerId, setModifyFollowerId] = React.useState(0);
  const [modifyName, setModifyName] = React.useState('');
  const [modifyDesc, setModifyDesc] = React.useState('');

  const addHandleClickOpen = () => {
    setOpenAdd(true);
  };

  const addHandleClose = () => {
    setOpenAdd(false);
  };

  const modifyHandleClose = () => {
    setOpenModify(false);
  };

  const getUid = () => {
    return cookies.uid;
  };

  const addFollower = async (name, desc) => {
    console.log(name, desc);

    axios.defaults.withCredentials = true;
    axios
      .put(process.env.REACT_APP_API_URL + '/follower', {
        nickName: name,
        description: desc,
        uid: getUid()
      })
      .then((response) => {
        if (response.data.message != 'OK') {
          alert(response.data?.message);
        } else {
          // 만약 response.data?.taskId 가 null 이 아니면 setCreatedTaskId(response.data?.taskId); 호출
        }
        getFollowers();
      })
      .catch(function (error) {
        if (error?.data?.message == null) {
          alert(error);
        } else {
          alert(error.data.message);
        }
      });
  };

  const modifyFollower = (followerId, name, desc) => {
    axios
      .post(process.env.REACT_APP_API_URL + '/follower', {
        followerId: followerId,
        nickName: name,
        description: desc,
        uid: getUid()
      })
      .then((response) => {
        console.log(response);
        if (response.data.message != 'OK') {
          alert(response.data?.message);
        } else {
          getFollowers();
        }
      })
      .catch(function (error) {
        if (error?.data?.message == null) {
          alert(error);
        } else {
          alert(error.data.message);
        }
      });

    setOpenModify(false);
  };

  const modifyDialogOpen = (follower) => {
    setModifyFollowerId(follower.followerId);
    setModifyName(follower.nickName);
    setModifyDesc(follower.description);
    setOpenModify(true);
  };

  const deleteFollower = (followerId) => {
    console.log('deleteFollower: ' + followerId);
    const url = process.env.REACT_APP_API_URL + '/follower/' + followerId + '/' + getUid();
    axios.defaults.withCredentials = true;
    axios
      .delete(url)
      .then((response) => {
        console.log(response);
        if (response.data.message != 'OK') {
          alert(response.data?.message);
        } else {
          getFollowers();
        }
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

  const getFollowers = async () => {
    console.log('getFollowers');
    axios.defaults.withCredentials = true;
    const urlStr = process.env.REACT_APP_API_URL + '/follower/followers?uid=' + getUid();

    // alert(urlStr);

    try {
      const response = await axios.get(urlStr);

      setFollowers(response.data.followerList);
    } catch (e) {
      console.error(e);
      alert(e);
    }
  };

  const refresh = () => {
    console.log('refresh');
    console.log(cookies.uid);
    getFollowers();
  };
  const modify = () => {
    console.log('nextDayPick');
  };

  useEffect(() => {
    getFollowers();
    return () => {};
  }, []);

  return (
    <MainCard title="구성원 관리">
      <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="en">
        <Box sx={{ flexGrow: 1 }}>
          <Grid container spacing={1}>
            <Grid item xs={1}>
              <Button size="small" variant="contained" fullWidth onClick={addHandleClickOpen}>
                <AddIcon />
              </Button>
            </Grid>
            <Grid item xs={1}>
              <Button size="small" variant="contained" fullWidth onClick={refresh}>
                <RefreshIcon />
              </Button>
            </Grid>
            <Grid item xs={1}>
              <Button size="small" variant="contained" fullWidth onClick={modify}>
                <EditIcon />
              </Button>
            </Grid>
            <Grid item xs={9}></Grid>
          </Grid>

          <TableContainer component={Paper}>
            <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
              <TableHead>
                <TableRow>
                  <TableCell>Id</TableCell>
                  <TableCell align="right">이름</TableCell>
                  <TableCell align="right">Desc</TableCell>
                  <TableCell align="center">액션</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {followers &&
                  followers.map((follower) => (
                    <TableRow key={follower.followerId} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                      <TableCell component="th" scope="row">
                        {follower.followerId}
                      </TableCell>
                      <TableCell align="right">{follower.nickName}</TableCell>
                      <TableCell align="right">{follower.description}</TableCell>
                      <TableCell align="center">
                        {/*<Button onClick={deleteFollower(follower.followerId)}>삭제</Button>*/}
                        <Button onClick={() => modifyDialogOpen(follower)}>수정</Button>
                        <Button onClick={() => deleteFollower(follower.followerId)}>삭제</Button>
                      </TableCell>
                    </TableRow>
                  ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Box>
      </LocalizationProvider>

      <Dialog
        open={openModify}
        onClose={modifyHandleClose}
        PaperProps={{
          component: 'form',
          onSubmit: (event) => {
            event.preventDefault();
            const formData = new FormData(event.currentTarget);
            const formJson = Object.fromEntries(formData.entries());
            const name = formJson.nickName;
            const desc = formJson.description;
            modifyFollower(modifyFollowerId, modifyName, modifyDesc);
            console.log(name);
            console.log(desc);
            addHandleClose();
          }
        }}
      >
        <DialogTitle>구성원 수정</DialogTitle>
        <DialogContent>
          <DialogContentText>수정할 구성원의 내용을 입력하세요. (id: {modifyFollowerId} )</DialogContentText>
          <TextField
            autoFocus
            required
            margin="dense"
            id="nickName"
            name="nickName"
            label="Nick Nanme"
            fullWidth
            variant="standard"
            defaultValue={modifyName}
            onChange={(e) => {
              setModifyName(e.target.value);
            }}
          />
          <TextField
            required
            margin="dense"
            id="description"
            name="description"
            label="Description"
            fullWidth
            variant="standard"
            defaultValue={modifyDesc}
            onChange={(e) => {
              setModifyDesc(e.target.value);
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={modifyHandleClose}>Cancel</Button>
          <Button type="submit">Subscribe</Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={openAdd}
        onClose={addHandleClose}
        PaperProps={{
          component: 'form',
          onSubmit: (event) => {
            event.preventDefault();
            const formData = new FormData(event.currentTarget);
            const formJson = Object.fromEntries(formData.entries());
            const name = formJson.nickName;
            const desc = formJson.description;
            addFollower(name, desc);
            console.log(name);
            console.log(desc);
            addHandleClose();
          }
        }}
      >
        <DialogTitle>구성원 추가</DialogTitle>
        <DialogContent>
          <DialogContentText>추가할 구성원을 입력하세요.</DialogContentText>
          <TextField autoFocus required margin="dense" id="nickName" name="nickName" label="Nick Nanme" fullWidth variant="standard" />
          <TextField required margin="dense" id="description" name="description" label="Description" fullWidth variant="standard" />
        </DialogContent>
        <DialogActions>
          <Button onClick={addHandleClose}>Cancel</Button>
          <Button type="submit">Subscribe</Button>
        </DialogActions>
      </Dialog>
    </MainCard>
  );
}
