// Dependencies
import React from 'react';

// Styles
import '../styles/Components/AboutUs.css';

// Components
// Pictures
import placeholder from '../assets/Creators/placeholder.jpg';
import david from '../assets/Creators/IMG_4276.jpg';
import domen from '../assets/Creators/domen.jpg';

// Icons
import linkedIn from '../assets/Icons/linkedin.png';
// Linked in link -> <a href="https://www.flaticon.com/free-icons/linkedin" title="linkedin icons">Linkedin icons created by riajulislam - Flaticon</a>
import steam from '../assets/Icons/steam.png';
// Stea in link -> <a href="https://www.flaticon.com/free-icons/steam" title="steam icons">Steam icons created by Hight Quality Icons - Flaticon</a>
import discord from '../assets/Icons/discord.png';
// Discord in link -> <a href="https://www.flaticon.com/free-icons/discord" title="discord icons">Discord icons created by Freepik - Flaticon</a>
import github from '../assets/Icons/github.png';
// Github in link -> <a href="https://www.flaticon.com/free-icons/github" title="github icons">Github icons created by Freepik - Flaticon</a>


const AboutUs = () => {
  return (
    <div className="blueBackground">
      <h2 className="aboutUsTitle">Meet our team</h2>
      <div className="aboutUsLayout">
      <ProfileComponent
          name="Andraž Šošterič"
          role="Project Leader"
          description=" 
          Andraž is a Developer specializing in Kotlin and Java. <br />
          His responsibilities are project management, mobile application and backend integration.<br /> </br />
          He manages Azure and Docker, project hierarchy, overall project documentation and Git in our project. </br/> <br/>
          In his free time he enjoys discussing CS2 pros and skin combinations with Domen while playing BTD6.
          "
          profilePicture={placeholder}
          contacts={[
            { name: 'Github', url: 'https://github.com/Asos75', icon: github },
            { name: 'Steam', url: 'https://steamcommunity.com/id/asos75/', icon: steam }
          ]}
        />
        <ProfileComponent
          name="David Rajlič"
          role="Backend developer"
          profilePicture={david}
          description="
          David is an overall programmer that uses Linux. <br />
          His responsibilities are backend infastructure, data parsing and database development. <br /> <br />
          He manages MongoDB, Express server, API endpoints, data integrity and overall backend development. <br /> <br />
          His favourite football team is FC Bayern and he is a better chess player than Domen.
          "
          contacts={[
            { name: 'Github', url: 'https://github.com/DavidRajlic', icon: github },
            { name: 'LinkedIn', url: 'https://www.linkedin.com/in/david-rajlic-07360a266/', icon: linkedIn }
          ]}
        />
        <ProfileComponent
          name="Domen Pahole"
          role="Frontend developer"
          profilePicture={domen}
          description="
          The best words to describe him are Frontend Wizard. <br />
          His responsibilites are frontend development, UI/UX design, backend connectivity and optimization. <br /> <br />
          He manages React, CSS, HTML, JS, HTTP requests, color scheme and overall frontend structure. <br /> <br />
          He spends his time working out, sharpening his eye to detail and going to McDonalds.
          "
          contacts={[
            { name: 'LinkedIn', url: 'https://www.linkedin.com/in/domenpahole/', icon: linkedIn },
            { name: 'Steam', url: 'https://steamcommunity.com/id/DomenP/', icon: steam }
          ]}
        />
      </div>
    </div>
  );
};

const ProfileComponent = ({ name, role, description, profilePicture, contacts }) => {
  return (
    <div className="profile">
      <div className="profileComponentHeader">
        <img src={profilePicture} alt={name} className="profilePicture"/>
        <div className="profileDetails">
          <h3 className="profileHeader">{name}</h3>
          <p className="profileRole">{role}</p>
        </div>
      </div>
      <hr className="profileDevider"/>
      <p className="profileDescription" dangerouslySetInnerHTML={{ __html: description }}></p>
      <div className="profileContacts">
        { contacts && contacts.map((contact, index) => (
          <a key={index} href={contact.url} target="_blank" rel="noopener noreferrer">
            <img src={contact.icon} alt={contact.name} />
          </a>
        ))}
      </div>
    </div>
  );
};


export default AboutUs;