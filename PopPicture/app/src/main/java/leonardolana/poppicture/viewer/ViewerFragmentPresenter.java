package leonardolana.poppicture.viewer;

import leonardolana.poppicture.common.BasePresenter;
import leonardolana.poppicture.data.Picture;
import leonardolana.poppicture.helpers.api.CloudStorage;
import leonardolana.poppicture.helpers.api.ServerHelper;
import leonardolana.poppicture.helpers.api.UserHelper;
import leonardolana.poppicture.server.RequestError;
import leonardolana.poppicture.server.RequestResponse;
import leonardolana.poppicture.server.ServerRequestRemovePicture;

/**
 * Created by Leonardo Lana
 * Github: https://github.com/leonardodlana
 * <p>
 * Copyright 2018 Leonardo Lana
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class ViewerFragmentPresenter extends BasePresenter {

    private ViewerFragmentView mView;
    private Picture mPicture;
    private ServerHelper mServerHelper;
    private UserHelper mUserHelper;
    private CloudStorage mCloudStorage;

    public ViewerFragmentPresenter(ViewerFragmentView view, Picture picture, ServerHelper serverHelper,
                                   UserHelper userHelper, CloudStorage cloudStorage) {
        mView = view;
        mPicture = picture;
        mServerHelper = serverHelper;
        mUserHelper = userHelper;
        mCloudStorage = cloudStorage;
    }

    public void onCloseClick() {
        mView.dismiss();
    }

    public void onDeleteClick() {
        mCloudStorage.delete(new CloudStorage.OnDeleteListener() {
            @Override
            public void onCompletion() {
                new ServerRequestRemovePicture(mPicture.getId()).execute(mServerHelper, mUserHelper, new RequestResponse() {
                    @Override
                    public void onRequestSuccess(String data) {
                        mView.dismiss();
                    }

                    @Override
                    public void onRequestError(RequestError error) {

                    }
                });
            }

            @Override
            public void onError() {

            }
        }, Picture.getPath(mPicture), Picture.getThumbPath(mPicture));
    }

    public void onLikeClick() {

    }

    @Override
    public void onDestroy() {
        mView = null;
        mPicture = null;
        mServerHelper = null;
        mUserHelper = null;
        mCloudStorage = null;
    }


}
