// Helps format time from (5/15/2024, 7:24:19 PM) -> (19:24 15.05.2024) format
const formatTime = (time) => {
  const date = new Date(time);
  const timeString = date.toLocaleTimeString('it-IT', { hour: '2-digit', minute: '2-digit' });
  const dateString = date.toLocaleDateString('en-GB', { day: '2-digit', month: '2-digit', year: 'numeric' }).split('/').join('.');
  return `${timeString} ${dateString}`;
};
  
// Calculate the distance between two points
const calculateDistance = (point1, point2) => {
  const [x1, y1] = point1;
  const [x2, y2] = point2;

  return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
};

// Calculate time dfference between two dates
const getTimeDifference = (eventTime) => {
  const eventDate = new Date(eventTime);
  const now = new Date();
  const diffInMilliseconds = eventDate - now;

  if (diffInMilliseconds < 0) {
    return 'Event has passed';
  }

  const diffInHours = diffInMilliseconds / (1000 * 60 * 60);
  const diffInDays = Math.floor(diffInHours / 24);

  if (diffInDays > 0) {
    return `Starts in ${diffInDays} day${diffInDays > 1 ? 's' : ''}`;
  }

  return `Starts in ${Math.floor(diffInHours)} hour${diffInHours > 1 ? 's' : ''}`;
};
  
export { formatTime, calculateDistance, getTimeDifference };