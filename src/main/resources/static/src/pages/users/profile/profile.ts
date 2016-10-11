import User = Models.User;
import {IProfileService, ProfileService} from "../../../service/profileService";
import {inject} from "aurelia-framework";
/**
 * Created by mariusz on 10.10.16.
 */

@inject(ProfileService)
export class Profile {
    avatar:any[];
    currentLoggedUser:User;
    private profileService:IProfileService;

    constructor(profileService:IProfileService) {
        this.avatar = [];
        this.profileService = profileService;
    }

    activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
    }

    changeAvatar() {
        let reader = new FileReader();
        reader.readAsBinaryString(this.avatar[0]);
        reader.onload = () => {
            this.profileService.changeUserAvatar(
                this.currentLoggedUser.id, {
                    fileName: this.avatar[0].name,
                    bytes: reader.readAsDataURL(this.avatar[0])
                }
            ).then(avatar => this.currentLoggedUser.avatar = avatar, error => alert(error))
        };
    };
}

