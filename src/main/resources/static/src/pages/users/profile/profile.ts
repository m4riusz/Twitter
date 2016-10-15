import User = Models.User;
import {IProfileService, ProfileService} from "../../../service/profileService";
import {inject} from "aurelia-framework";


/**
 * Created by mariusz on 10.10.16.
 */

@inject(ProfileService)
export class Profile {
    currentLoggedUser:User;
    selectedFiles:any[];
    image:any;
    private profileService:IProfileService;

    constructor(profileService:IProfileService) {
        this.selectedFiles = [];
        this.profileService = profileService;
    }

    activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
    }

    openFile() {
        document.getElementById("fileInput").click();
    }

    changeAvatar() {
        this.image = this.selectedFiles[0];
        let reader = new FileReader();

        reader.onload = (event)=> {
            this.profileService.changeUserAvatar(this.currentLoggedUser.id, {
                fileName: this.image.name,
                bytes: event.target.result
            })
                .then(avatar => {
                    this.currentLoggedUser.avatar = avatar;
                    this.image = "";
                }, error => alert(error));
        };
        reader.readAsDataURL(this.image);
    };

    async changePassword(password:string, rePassword:string) {
        try {
            if (password === rePassword) {
                await this.profileService.changeUserPassword(this.currentLoggedUser.id, password);
                alert("You have changed password!");
            } else {
                alert("Passwords aren't equal!");
            }
        } catch (error) {
            alert(error);
        }
    }

    async changeEmail(email:string, reEmail:string) {
        try {
            if (email === reEmail) {
                await this.profileService.changeUserEmail(this.currentLoggedUser.id, email);
                this.currentLoggedUser.email = email;
                alert("You have changed email!");
            } else {
                alert("Emails aren't equal!");
            }
        } catch (error) {
            alert(error);
        }
    }


}

